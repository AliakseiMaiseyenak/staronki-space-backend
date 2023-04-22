package by.hackaton.bookcrossing.service;

import by.hackaton.bookcrossing.dto.*;
import by.hackaton.bookcrossing.dto.response.AccountShortResponse;
import by.hackaton.bookcrossing.dto.response.BookOnMapResponse;
import by.hackaton.bookcrossing.dto.response.OnMapResponse;
import by.hackaton.bookcrossing.entity.Account;
import by.hackaton.bookcrossing.entity.Book;
import by.hackaton.bookcrossing.entity.BookOrder;
import by.hackaton.bookcrossing.entity.BookRequest;
import by.hackaton.bookcrossing.entity.enums.BookStatus;
import by.hackaton.bookcrossing.repository.AccountRepository;
import by.hackaton.bookcrossing.repository.BookRepository;
import by.hackaton.bookcrossing.repository.BookRequestRepository;
import by.hackaton.bookcrossing.repository.OrderRepository;
import by.hackaton.bookcrossing.service.exceptions.BadRequestException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class BookService {

    private final String URL = "http://classify.oclc.org/classify2/Classify?isbn=%s&summary=true";

    @PersistenceContext
    private EntityManager em;

    private BookRepository bookRepository;
    private BookRequestRepository bookRequestRepository;
    private AccountRepository accountRepository;
    private OrderRepository orderRepository;
    private ModelMapper modelMapper;

    public BookService(BookRepository bookRepository, BookRequestRepository bookRequestRepository,
                       AccountRepository accountRepository, OrderRepository orderRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.bookRequestRepository = bookRequestRepository;
        this.accountRepository = accountRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    public List<BookDto> getBooks() {
        return bookRepository.findByStatusAVAILABLE().stream().map(b -> modelMapper.map(b, BookDto.class)).collect(toList());
    }

    public List<OnMapResponse> getBooksWithUser() {
        List<Account> accounts = accountRepository.findAll();
        Map<AccountShortResponse, List<Book>> mapa = accounts.stream().collect(Collectors.toMap(a -> modelMapper.map(a, AccountShortResponse.class), Account::getBooks));
        List<OnMapResponse> list = new ArrayList<>();
        mapa.forEach((k, v) -> list.add(createBookWithUser(k, v)));
        return list;
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new BadRequestException("Book not found")
        );
        return modelMapper.map(book, BookDto.class);

    }

    public BookDto createBook(BookDto dto, String email) {
        Account owner  = accountRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found")
        );
        Book book = modelMapper.map(dto, Book.class);
        book.setOwner(owner);
        book.getAuthors().forEach(author -> author.setBook(book));
        bookRepository.save(book);
        return modelMapper.map(book, BookDto.class);
    }

    public BookDto updateBook(Long id, BookDto dto, String email) {
        Book book = bookRepository.findByIdAndOwner_Email(id, email).orElseThrow(
                () -> new BadRequestException("Book not found")
        );
        modelMapper.map(book, dto);
        bookRepository.save(book);
        return modelMapper.map(book, BookDto.class);
    }

    @Transactional
    public BookDto updateStatus(Long id, String email) {
        Book book = bookRepository.findByIdAndOwner_Email(id, email).orElseThrow(
                () -> new BadRequestException("Book not found")
        );
        BookStatus newStatus = book.getStatus().equals(BookStatus.AVAILABLE) ? BookStatus.NOT_AVAILABLE : BookStatus.AVAILABLE;
        book.setStatus(newStatus);
        bookRepository.save(book);
        return modelMapper.map(book, BookDto.class);
    }

    public List<String> autoComplete(BookFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> criteria = cb.createQuery(String.class);
        Root<Book> root = criteria.distinct(true).from(Book.class);
        switch (filter.getField()) {
            case TITLE:
                criteria.select(root.get("title")).where(cb.like(root.get("title"), "%" + filter.getText().toLowerCase() + "%"));
                break;
            case AUTHOR:
                criteria.select(root.get("author")).where(cb.like(root.get("author"), "%" + filter.getText().toLowerCase() + "%"));
                break;
            default:
                break;
        }
        return em.createQuery(criteria).getResultList();
    }

    private OnMapResponse createBookWithUser(AccountShortResponse account, List<Book> books) {
        return new OnMapResponse(account, books.stream().filter(b -> BookStatus.AVAILABLE.equals(b.getStatus()))
                .map(b -> modelMapper.map(b, BookOnMapResponse.class)).collect(toList()));
    }

    public BookShortDto findBookByISBN(String isbn) {
        Optional<Book> book = bookRepository.findByISBN(isbn);
        if (book.isPresent()) {
            return modelMapper.map(book, BookShortDto.class);
        }
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(String.format(URL, isbn))).build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(response.body().toString())));
            doc.getDocumentElement().normalize();


            XPath xpath = XPathFactory.newInstance().newXPath();
            Node respNode = getNode(xpath, doc);
            if (respNode != null) {
                String respCode = getAttribute(respNode, "@code");
                if ("4".equals(respCode)) {
                    String expression = "//works/work";
                    NodeList list = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
                    Node node = list.item(0);
                    String title = getAttribute(node, "@title");
                    String authorFullname = getAttribute(node, "@author");
                    AuthorDto author = new AuthorDto();
                    BookShortDto dto = new BookShortDto();
                    dto.setAuthors(List.of(author));
                    dto.setTitle(title);
                    dto.setISBN(isbn);
                    return dto;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Node getNode(XPath xpath, Node parent) {
        String expression = "//response";
        Node node = null;
        try {
            node = (Node) xpath.evaluate(expression, parent, XPathConstants.NODE);
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
        return node;
    }

    private String getAttribute(Node node, String expression) {
        String attrValue = null;
        try {
            NamedNodeMap attrs = node.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Node a = attrs.item(i);
                if (a.getNodeName().equals(expression.substring(1)))
                    attrValue = a.getNodeValue();
            }
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }

        return attrValue;
    }

    public void askBookById(Long id, SendTypeDto dto, String email) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new BadRequestException("Book not found"));
        Account account = accountRepository.findByEmail(email).orElseThrow(
                () -> new BadRequestException("Account not found"));
        BookRequest request = new BookRequest();
        request.setUser(account);
        request.setBook(book);
        request.setBookOwner(book.getOwner());
        request.setSendType(dto.sendType);
        bookRequestRepository.save(request);
    }

    public OrderDto getBookOrder(Long id, String email) {
        Optional<BookOrder> order = orderRepository.findByBook_IdAndActiveTrue(id);
        return order.map(bookOrder -> modelMapper.map(bookOrder, OrderDto.class)).orElse(null);
    }

    public void deleteBook(Long id) {
        log.info("Book with id=" + id + " ready for removing");
        bookRepository.deleteById(id);
    }
}
