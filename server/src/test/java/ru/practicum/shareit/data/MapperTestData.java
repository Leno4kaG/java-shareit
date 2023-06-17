package ru.practicum.shareit.data;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemClientDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserClientDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class MapperTestData {

    public static UserMapper getUserMapper() {
        return new UserMapper() {
            @Override
            public UserDto toDto(User user) {
                if (user == null) {
                    return null;
                }

                UserDto userDto = new UserDto();

                userDto.setId(user.getId());
                userDto.setName(user.getName());
                userDto.setEmail(user.getEmail());

                return userDto;
            }

            @Override
            public UserClientDto toClientDto(User user) {
                if (user == null) {
                    return null;
                }

                UserClientDto userDto = new UserClientDto();

                userDto.setId(user.getId());
                userDto.setEmail(user.getEmail());

                return userDto;
            }

            @Override
            public User fromDto(UserDto userDto) {
                if (userDto == null) {
                    return null;
                }

                User user = new User();

                user.setId(userDto.getId());
                user.setName(userDto.getName());
                user.setEmail(userDto.getEmail());

                return user;
            }

            @Override
            public User fromDto(UserClientDto userDto) {
                if (userDto == null) {
                    return null;
                }

                User user = new User();

                user.setId(userDto.getId());
                user.setEmail(userDto.getEmail());

                return user;
            }

            @Override
            public List<UserDto> toDtoList(List<User> userList) {
                if (userList == null) {
                    return null;
                }

                List<UserDto> list = new ArrayList<UserDto>(userList.size());
                for (User user : userList) {
                    list.add(toDto(user));
                }

                return list;
            }
        };
    }

    public static BookingMapper getBookingMapper() {
        return new BookingMapper() {
            @Override
            public BookingDto toBookingDto(BookingRequestDto bookingClientRequestDto) {
                if (bookingClientRequestDto == null) {
                    return null;
                }

                BookingDto bookingDto = new BookingDto();

                bookingDto.setStart(bookingClientRequestDto.getStart());
                bookingDto.setEnd(bookingClientRequestDto.getEnd());

                return bookingDto;
            }

            @Override
            public BookingDto toDto(Booking booking) {
                if (booking == null) {
                    return null;
                }

                BookingDto bookingDto = new BookingDto();

                if (booking.getId() != null) {
                    bookingDto.setId(booking.getId());
                }
                bookingDto.setStart(booking.getStart());
                bookingDto.setEnd(booking.getEnd());
                bookingDto.setItem(getItemMapper().toClientDto(booking.getItem()));
                bookingDto.setBooker(getUserMapper().toClientDto(booking.getBooker()));
                bookingDto.setStatus(booking.getStatus());

                return bookingDto;
            }

            @Override
            public BookingForItem toBookingForItem(Booking booking) {
                if (booking == null) {
                    return null;
                }

                BookingForItem bookingForItem = new BookingForItem();

                bookingForItem.setBookerId(bookingBookerId(booking));
                bookingForItem.setId(booking.getId());

                return bookingForItem;
            }

            @Override
            public Booking fromDto(BookingDto bookingClientDto) {
                if (bookingClientDto == null) {
                    return null;
                }

                Booking booking = new Booking();

                booking.setId(bookingClientDto.getId());
                booking.setStart(bookingClientDto.getStart());
                booking.setEnd(bookingClientDto.getEnd());
                booking.setItem(getItemMapper().fromClientDto(bookingClientDto.getItem()));
                booking.setBooker(getUserMapper().fromDto(bookingClientDto.getBooker()));
                booking.setStatus(bookingClientDto.getStatus());

                return booking;
            }

            @Override
            public List<BookingDto> toListDto(List<Booking> bookingList) {
                if (bookingList == null) {
                    return null;
                }

                List<BookingDto> list = new ArrayList<BookingDto>(bookingList.size());
                for (Booking booking : bookingList) {
                    list.add(toDto(booking));
                }

                return list;
            }

            private Long bookingBookerId(Booking booking) {
                if (booking == null) {
                    return null;
                }
                User booker = booking.getBooker();
                if (booker == null) {
                    return null;
                }
                long id = booker.getId();
                return id;
            }
        };
    }

    public static ItemMapper getItemMapper() {
        return new ItemMapper() {
            @Override
            public ItemDto toDto(Item item) {
                if (item == null) {
                    return null;
                }

                ItemDto itemDto = new ItemDto();

                itemDto.setRequestId(itemRequestId(item));
                itemDto.setId(item.getId());
                itemDto.setName(item.getName());
                itemDto.setDescription(item.getDescription());
                itemDto.setAvailable(item.getAvailable());

                return itemDto;
            }

            @Override
            public ItemClientDto toClientDto(Item item) {
                if (item == null) {
                    return null;
                }

                ItemClientDto itemDto = new ItemClientDto();

                itemDto.setId(item.getId());
                itemDto.setName(item.getName());

                return itemDto;
            }

            @Override
            public Item fromDto(ItemDto itemDto) {
                if (itemDto == null) {
                    return null;
                }

                Item item = new Item();

                item.setId(itemDto.getId());
                item.setName(itemDto.getName());
                item.setDescription(itemDto.getDescription());
                item.setAvailable(itemDto.getAvailable());

                return item;
            }

            @Override
            public Item fromClientDto(ItemClientDto itemClientDto) {
                if (itemClientDto == null) {
                    return null;
                }

                Item item = new Item();

                item.setId(itemClientDto.getId());
                item.setName(itemClientDto.getName());
                return item;
            }

            @Override
            public ItemWithBooking toItemWithBooking(Item item) {
                if (item == null) {
                    return null;
                }

                ItemWithBooking itemWithBooking = new ItemWithBooking();

                itemWithBooking.setRequestId(itemRequestId(item));
                itemWithBooking.setId(item.getId());
                itemWithBooking.setName(item.getName());
                itemWithBooking.setDescription(item.getDescription());
                itemWithBooking.setAvailable(item.getAvailable());

                return itemWithBooking;
            }

            @Override
            public List<ItemDto> toListDto(List<Item> itemList) {
                if (itemList == null) {
                    return null;
                }

                List<ItemDto> list = new ArrayList<ItemDto>(itemList.size());
                for (Item item : itemList) {
                    list.add(toDto(item));
                }

                return list;
            }

            @Override
            public List<ItemClientDto> toListClientDto(List<Item> itemList) {
                return null;
            }

            private Long itemRequestId(Item item) {
                if (item == null) {
                    return null;
                }
                ItemRequest request = item.getRequest();
                if (request == null) {
                    return null;
                }
                long id = request.getId();
                return id;
            }
        };
    }

    public static ItemRequestMapper gerReqMapper() {
        return new ItemRequestMapper() {
            @Override
            public ItemRequestDto toDto(ItemRequest itemRequest) {
                if (itemRequest == null) {
                    return null;
                }

                ItemRequestDto itemRequestDto = new ItemRequestDto();

                itemRequestDto.setId(itemRequest.getId());
                itemRequestDto.setDescription(itemRequest.getDescription());
                itemRequestDto.setCreated(itemRequest.getCreated());

                return itemRequestDto;
            }

            @Override
            public RequestInfoDto toInfoDto(ItemRequest itemRequest) {
                if (itemRequest == null) {
                    return null;
                }

                RequestInfoDto requestInfoDto = new RequestInfoDto();

                requestInfoDto.setRequestId(itemRequestRequestId(itemRequest));
                requestInfoDto.setId(itemRequest.getId());
                requestInfoDto.setDescription(itemRequest.getDescription());
                requestInfoDto.setCreated(itemRequest.getCreated());

                return requestInfoDto;
            }

            @Override
            public ItemRequest fromDto(ItemRequestDto itemRequestDto) {
                if (itemRequestDto == null) {
                    return null;
                }

                ItemRequest itemRequest = new ItemRequest();

                itemRequest.setId(itemRequestDto.getId());
                itemRequest.setDescription(itemRequestDto.getDescription());
                itemRequest.setCreated(itemRequestDto.getCreated());

                return itemRequest;
            }

            @Override
            public List<ItemRequestDto> toListDto(List<ItemRequest> itemRequests) {
                if (itemRequests == null) {
                    return null;
                }

                List<ItemRequestDto> list = new ArrayList<ItemRequestDto>(itemRequests.size());
                for (ItemRequest itemRequest : itemRequests) {
                    list.add(toDto(itemRequest));
                }

                return list;
            }

            @Override
            public List<RequestInfoDto> toListInfoDto(List<ItemRequest> itemRequests) {
                if (itemRequests == null) {
                    return null;
                }

                List<RequestInfoDto> list = new ArrayList<RequestInfoDto>(itemRequests.size());
                for (ItemRequest itemRequest : itemRequests) {
                    list.add(toInfoDto(itemRequest));
                }

                return list;
            }

            private Long itemRequestRequestId(ItemRequest itemRequest) {
                if (itemRequest == null) {
                    return null;
                }
                User request = itemRequest.getRequest();
                if (request == null) {
                    return null;
                }
                long id = request.getId();
                return id;
            }
        };
    }

    public static CommentMapper getCommentMapper() {
        return new CommentMapper() {
            @Override
            public Comment fromDto(CommentDto commentDto) {
                if (commentDto == null) {
                    return null;
                }

                Comment comment = new Comment();

                if (commentDto.getId() != null) {
                    comment.setId(commentDto.getId());
                }
                comment.setText(commentDto.getText());
                comment.setCreated(commentDto.getCreated());

                return comment;
            }

            @Override
            public CommentDto toDto(Comment comment) {
                if (comment == null) {
                    return null;
                }

                CommentDto commentDto = new CommentDto();

                commentDto.setAuthorName(commentAuthorName(comment));
                commentDto.setId(comment.getId());
                commentDto.setText(comment.getText());
                commentDto.setCreated(comment.getCreated());

                return commentDto;
            }

            @Override
            public List<CommentDto> toListDto(List<Comment> comments) {
                if (comments == null) {
                    return null;
                }

                List<CommentDto> list = new ArrayList<CommentDto>(comments.size());
                for (Comment comment : comments) {
                    list.add(toDto(comment));
                }

                return list;
            }

            private String commentAuthorName(Comment comment) {
                if (comment == null) {
                    return null;
                }
                User author = comment.getAuthor();
                if (author == null) {
                    return null;
                }
                String name = author.getName();
                if (name == null) {
                    return null;
                }
                return name;
            }
        };
    }
}
