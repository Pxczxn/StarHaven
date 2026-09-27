package top.pxczxn.business.bootstrap;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.business.entity.Banner;
import top.pxczxn.business.entity.BookingOrder;
import top.pxczxn.business.entity.Comment;
import top.pxczxn.business.entity.Coupon;
import top.pxczxn.business.entity.CouponUser;
import top.pxczxn.business.entity.Favorite;
import top.pxczxn.business.entity.House;
import top.pxczxn.business.entity.HouseFacility;
import top.pxczxn.business.entity.HouseImage;
import top.pxczxn.business.entity.Message;
import top.pxczxn.business.mapper.BannerMapper;
import top.pxczxn.business.mapper.BookingOrderMapper;
import top.pxczxn.business.mapper.CommentMapper;
import top.pxczxn.business.mapper.CouponMapper;
import top.pxczxn.business.mapper.CouponUserMapper;
import top.pxczxn.business.mapper.HouseFacilityMapper;
import top.pxczxn.business.mapper.HouseImageMapper;
import top.pxczxn.business.mapper.HouseMapper;
import top.pxczxn.business.mapper.FavoriteMapper;
import top.pxczxn.business.mapper.MessageMapper;
import top.pxczxn.common.constant.RedisKeys;
import top.pxczxn.common.redis.RedisFacade;
import top.pxczxn.system.entity.User;
import top.pxczxn.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {

    private static final String STORE = "星栖民宿";

    /** 演示评价：{分数, 内容} */
    private static final String[][] DEMO_COMMENTS = {
        { "5", "落地窗视野比照片还好，床品干净，晚上很安静，会再来。" },
        { "5", "房东回复很快，入住指引清楚，位置也方便，整体超出预期。" },
        { "4", "房间整洁、设施齐全，唯一小缺点是热水要放一会儿才热。" },
        { "5", "带家人一起住，空间够用，厨房能做饭，性价比很高。" },
        { "4", "环境不错，交通便利；隔音一般，夜里能听到走廊声音。" },
        { "5", "第二次入住了，依旧稳定发挥，下次出差还订这家。" },
    };

    private final UserMapper userMapper;
    private final HouseMapper houseMapper;
    private final FavoriteMapper favoriteMapper;
    private final HouseImageMapper houseImageMapper;
    private final HouseFacilityMapper houseFacilityMapper;
    private final BannerMapper bannerMapper;
    private final CouponMapper couponMapper;
    private final CouponUserMapper couponUserMapper;
    private final CommentMapper commentMapper;
    private final MessageMapper messageMapper;
    private final BookingOrderMapper bookingOrderMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisFacade redisFacade;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        User admin = findOrCreateUser("admin", "平台管理员", "13800000000", "ADMIN");
        User merchant = findOrCreateUser("host", "星栖商家", "13800000002", "HOST");
        User guest = findOrCreateUser("staruser", "入住客人", "13800000001", "USER");

        List<Long> houseIds = new ArrayList<>();
        House sea = upsertHouse(merchant.getId(), List.of("海边日落·精品民宿"),
                "海景木屋 A 栋", "面朝水面的独立木屋，适合双人入住。",
                "A 栋 01", new BigDecimal("568.00"), "WHOLE", 4, 2, 2, 2, 4.9, 28, 96,
                "/houses/house-sanya-cover.png",
                List.of("/houses/house-sanya-cover.png", "/houses/house-interior-ocean.png", "/houses/house-interior-lake.png"),
                List.of("WiFi", "停车", "厨房", "空调", "泳池"));
        houseIds.add(sea.getId());
        House garden = upsertHouse(merchant.getId(), List.of("京都町屋·温泉庭院"),
                "庭院套房 2 号", "带独立庭院的套房，适合家庭小住。",
                "主楼 2 号", new BigDecimal("498.00"), "WHOLE", 3, 1, 1, 2, 4.8, 19, 80,
                "/houses/house-suzhou-cover.png",
                List.of("/houses/house-suzhou-cover.png", "/houses/house-interior-courtyard.png", "/houses/house-interior-lake.png"),
                List.of("WiFi", "厨房", "空调", "早餐"));
        houseIds.add(garden.getId());
        House mountain = upsertHouse(merchant.getId(), List.of("北海道雪屋·星空木屋"),
                "山景家庭房", "可观星的家庭房，含两张大床。",
                "山景楼 3 层", new BigDecimal("688.00"), "WHOLE", 5, 2, 2, 3, 4.7, 15, 72,
                "/houses/house-dali-cover.png",
                List.of("/houses/house-dali-cover.png", "/houses/house-interior-lake.png", "/houses/house-interior-courtyard.png"),
                List.of("WiFi", "停车", "厨房", "空调"));
        houseIds.add(mountain.getId());
        House suite = upsertHouse(merchant.getId(), List.of("冲绳蓝湾·海景套房"),
                "临水套房", "酒店式套房，步行即可到达公共泳池。",
                "临水楼 105", new BigDecimal("428.00"), "HOTEL", 2, 1, 1, 1, 4.6, 22, 64,
                "/houses/house-xiamen-cover.png",
                List.of("/houses/house-xiamen-cover.png", "/houses/house-interior-ocean.png", "/houses/house-interior-city.png"),
                List.of("WiFi", "空调", "早餐", "泳池"));
        houseIds.add(suite.getId());
        houseIds.add(upsertHouse(merchant.getId(), List.of(),
                "湖景阁楼", "挑高阁楼，落地窗朝向内湖。",
                "阁楼 201", new BigDecimal("538.00"), "WHOLE", 3, 1, 1, 2, 4.8, 12, 70,
                "/houses/house-hangzhou-cover.png",
                List.of("/houses/house-hangzhou-cover.png", "/houses/house-interior-lake.png", "/houses/house-interior-city.png"),
                List.of("WiFi", "厨房", "空调")).getId());
        houseIds.add(upsertHouse(merchant.getId(), List.of(),
                "花园独栋", "带小院的独栋，适合聚会。",
                "花园 1 号", new BigDecimal("758.00"), "WHOLE", 6, 3, 2, 4, 4.9, 9, 88,
                "/houses/house-chengdu-cover.png",
                List.of("/houses/house-chengdu-cover.png", "/houses/house-interior-courtyard.png", "/houses/house-interior-city.png"),
                List.of("WiFi", "停车", "厨房", "泳池")).getId());
        houseIds.add(upsertHouse(merchant.getId(), List.of(),
                "林间小屋", "安静的独立房间，适合差旅。",
                "林间 08", new BigDecimal("328.00"), "ROOM", 2, 1, 1, 1, 4.5, 16, 55,
                "/houses/house-beijing-cover.png",
                List.of("/houses/house-beijing-cover.png", "/houses/house-interior-courtyard.png", "/houses/house-interior-city.png"),
                List.of("WiFi", "空调")).getId());
        houseIds.add(upsertHouse(merchant.getId(), List.of(),
                "都市loft", "开放式 loft，适合短住办公。",
                "主楼 loft", new BigDecimal("458.00"), "WHOLE", 2, 1, 1, 1, 4.6, 11, 61,
                "/houses/house-shanghai-cover.png",
                List.of("/houses/house-shanghai-cover.png", "/houses/house-interior-city.png", "/houses/house-interior-ocean.png"),
                List.of("WiFi", "厨房", "空调")).getId());

        seedOrders(guest.getId(), sea.getId(), garden.getId());
        seedFavorites(guest.getId(), sea.getId(), garden.getId(), mountain.getId(), suite.getId());
        seedComments(guest.getId(), houseIds);
        seedBanner();
        seedCoupon(guest.getId());
        seedWelcomeMessage(merchant.getId());
        redisFacade.delete(RedisKeys.HOUSE_HOT);

        log.info("商家演示数据已就绪 admin/host/staruser 密码均为 123456, adminId={}, merchantId={}, guestId={}",
                admin.getId(), merchant.getId(), guest.getId());
    }

    private User findOrCreateUser(String username, String nickname, String phone, String role) {
        User existing = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (existing != null) {
            existing.setNickname(nickname);
            existing.setRole(role);
            existing.setStatus(1);
            userMapper.updateById(existing);
            return existing;
        }
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole(role);
        user.setStatus(1);
        user.setGender(0);
        user.setAvatar("/houses/house-interior-city.png");
        userMapper.insert(user);
        return user;
    }

    private House upsertHouse(Long hostId, List<String> aliases, String title, String desc, String address,
                              BigDecimal price, String type, int guest, int room, int bath, int bed,
                              double score, int comments, int heat, String cover, List<String> images, List<String> facilities) {
        House house = houseMapper.selectOne(Wrappers.<House>lambdaQuery().eq(House::getTitle, title));
        if (house == null) {
            for (String alias : aliases) {
                house = houseMapper.selectOne(Wrappers.<House>lambdaQuery().eq(House::getTitle, alias));
                if (house != null) {
                    break;
                }
            }
        }
        boolean insert = house == null;
        if (insert) {
            house = new House();
        }
        house.setHostId(hostId);
        house.setTitle(title);
        house.setCoverImage(cover);
        house.setDescription(desc);
        house.setCity(STORE);
        house.setAddress(address);
        house.setPrice(price);
        house.setHouseType(type);
        house.setGuestNumber(guest);
        house.setRoomNumber(room);
        house.setBathroomNumber(bath);
        house.setBedNumber(bed);
        house.setStatus(1);
        house.setAuditStatus(1);
        house.setAvgScore(BigDecimal.valueOf(score));
        house.setCommentCount(comments);
        house.setHeat(heat);
        house.setLatitude(new BigDecimal("30.250000"));
        house.setLongitude(new BigDecimal("120.150000"));
        if (insert) {
            houseMapper.insert(house);
        } else {
            houseMapper.updateById(house);
            redisFacade.delete(RedisKeys.HOUSE_DETAIL + house.getId());
        }
        replaceImages(house.getId(), images);
        replaceFacilities(house.getId(), facilities);
        return house;
    }

    private void replaceImages(Long houseId, List<String> urls) {
        houseImageMapper.delete(Wrappers.<HouseImage>lambdaQuery().eq(HouseImage::getHouseId, houseId));
        int sort = 1;
        for (String url : urls) {
            HouseImage image = new HouseImage();
            image.setHouseId(houseId);
            image.setImageUrl(url);
            image.setSort(sort++);
            houseImageMapper.insert(image);
        }
    }

    private void replaceFacilities(Long houseId, List<String> names) {
        houseFacilityMapper.delete(Wrappers.<HouseFacility>lambdaQuery().eq(HouseFacility::getHouseId, houseId));
        for (String name : names) {
            HouseFacility facility = new HouseFacility();
            facility.setHouseId(houseId);
            facility.setFacilityName(name);
            houseFacilityMapper.insert(facility);
        }
    }

    private void seedOrders(Long guestId, Long paidHouseId, Long waitHouseId) {
        if (bookingOrderMapper.selectCount(null) > 0) {
            return;
        }
        BookingOrder paid = new BookingOrder();
        paid.setOrderNo("SH" + LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "1001");
        paid.setUserId(guestId);
        paid.setHouseId(paidHouseId);
        paid.setCheckInDate(LocalDate.now().plusDays(2));
        paid.setCheckOutDate(LocalDate.now().plusDays(4));
        paid.setGuestCount(2);
        paid.setRoomCount(1);
        paid.setContactName("陈女士");
        paid.setContactPhone("13800000001");
        paid.setHousePrice(new BigDecimal("1136.00"));
        paid.setServiceFee(new BigDecimal("68.16"));
        paid.setTotalAmount(new BigDecimal("1204.16"));
        paid.setOrderStatus("PAID");
        paid.setPaymentStatus(1);
        bookingOrderMapper.insert(paid);

        BookingOrder wait = new BookingOrder();
        wait.setOrderNo("SH" + LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "1002");
        wait.setUserId(guestId);
        wait.setHouseId(waitHouseId);
        wait.setCheckInDate(LocalDate.now().plusDays(7));
        wait.setCheckOutDate(LocalDate.now().plusDays(9));
        wait.setGuestCount(2);
        wait.setRoomCount(1);
        wait.setContactName("陈女士");
        wait.setContactPhone("13800000001");
        wait.setHousePrice(new BigDecimal("996.00"));
        wait.setServiceFee(new BigDecimal("59.76"));
        wait.setTotalAmount(new BigDecimal("1055.76"));
        wait.setOrderStatus("WAIT_PAY");
        wait.setPaymentStatus(0);
        bookingOrderMapper.insert(wait);
    }

    private void seedFavorites(Long guestId, Long... houseIds) {
        for (Long houseId : houseIds) {
            if (favoriteMapper.selectCount(Wrappers.<Favorite>lambdaQuery()
                    .eq(Favorite::getUserId, guestId)
                    .eq(Favorite::getHouseId, houseId)) > 0) {
                continue;
            }
            Favorite favorite = new Favorite();
            favorite.setUserId(guestId);
            favorite.setHouseId(houseId);
            favoriteMapper.insert(favorite);
        }
    }

    private void seedComments(Long guestId, List<Long> houseIds) {
        long orderSeq = 910000L;
        for (Long houseId : houseIds) {
            if (commentMapper.selectCount(Wrappers.<Comment>lambdaQuery().eq(Comment::getHouseId, houseId)) > 0) {
                continue;
            }
            for (String[] item : DEMO_COMMENTS) {
                Comment comment = new Comment();
                comment.setUserId(guestId);
                comment.setHouseId(houseId);
                comment.setOrderId(orderSeq++);
                comment.setScore(Integer.parseInt(item[0]));
                comment.setContent(item[1]);
                comment.setCreateTime(LocalDateTime.now().minusDays(DEMO_COMMENTS.length + 1L - Integer.parseInt(item[0])));
                commentMapper.insert(comment);
            }
            refreshHouseScore(houseId);
        }
    }

    /**
     * 用真实评论重算房源评分与评价数，覆盖 upsertHouse 写入的预设值
     */
    private void refreshHouseScore(Long houseId) {
        List<Comment> comments = commentMapper.selectList(Wrappers.<Comment>lambdaQuery().eq(Comment::getHouseId, houseId));
        House house = houseMapper.selectById(houseId);
        if (house == null) {
            return;
        }
        double avg = comments.stream().mapToInt(Comment::getScore).average().orElse(0);
        house.setAvgScore(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        house.setCommentCount(comments.size());
        houseMapper.updateById(house);
        redisFacade.delete(RedisKeys.HOUSE_DETAIL + houseId);
    }

    private void seedBanner() {
        Banner banner = bannerMapper.selectOne(Wrappers.<Banner>lambdaQuery().eq(Banner::getTitle, "发现更多美好旅程"));
        if (banner == null) {
            banner = bannerMapper.selectOne(Wrappers.<Banner>lambdaQuery().eq(Banner::getTitle, "今日房态一览"));
        }
        if (banner == null) {
            banner = new Banner();
        }
        banner.setTitle("发现更多美好旅程");
        banner.setSubtitle("挑选今晚想住的房间");
        banner.setImageUrl("/banners/banner-starhaven.png");
        banner.setLinkUrl("/pages/listing/listing");
        banner.setSort(1);
        banner.setStatus(1);
        if (banner.getId() == null) {
            bannerMapper.insert(banner);
        } else {
            bannerMapper.updateById(banner);
        }
    }

    private void seedCoupon(Long guestId) {
        if (couponMapper.selectCount(null) > 0) {
            return;
        }
        Coupon coupon = new Coupon();
        coupon.setName("新客立减 50");
        coupon.setDiscount(new BigDecimal("50.00"));
        coupon.setConditionAmount(new BigDecimal("300.00"));
        coupon.setStartTime(LocalDateTime.now().minusDays(1));
        coupon.setEndTime(LocalDateTime.now().plusMonths(3));
        coupon.setStatus(1);
        couponMapper.insert(coupon);
        CouponUser couponUser = new CouponUser();
        couponUser.setCouponId(coupon.getId());
        couponUser.setUserId(guestId);
        couponUser.setUsed(0);
        couponUserMapper.insert(couponUser);
    }

    private void seedWelcomeMessage(Long merchantId) {
        if (messageMapper.selectCount(Wrappers.<Message>lambdaQuery().eq(Message::getUserId, merchantId)) > 0) {
            return;
        }
        Message message = new Message();
        message.setUserId(merchantId);
        message.setType("SYSTEM");
        message.setTitle("欢迎使用星栖商家端");
        message.setContent("今日可在工作台查看房源库存与预订。");
        message.setReadStatus(0);
        messageMapper.insert(message);
    }
}
