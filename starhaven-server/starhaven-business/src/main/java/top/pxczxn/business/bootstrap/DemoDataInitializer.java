package top.pxczxn.business.bootstrap;

import top.pxczxn.business.entity.Banner;
import top.pxczxn.business.entity.Comment;
import top.pxczxn.business.entity.Coupon;
import top.pxczxn.business.entity.CouponUser;
import top.pxczxn.business.entity.House;
import top.pxczxn.business.entity.HouseFacility;
import top.pxczxn.business.entity.HouseImage;
import top.pxczxn.business.entity.Message;
import top.pxczxn.business.mapper.BannerMapper;
import top.pxczxn.business.mapper.CommentMapper;
import top.pxczxn.business.mapper.CouponMapper;
import top.pxczxn.business.mapper.CouponUserMapper;
import top.pxczxn.business.mapper.HouseFacilityMapper;
import top.pxczxn.business.mapper.HouseImageMapper;
import top.pxczxn.business.mapper.HouseMapper;
import top.pxczxn.business.mapper.MessageMapper;
import top.pxczxn.system.entity.User;
import top.pxczxn.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final HouseMapper houseMapper;
    private final HouseImageMapper houseImageMapper;
    private final HouseFacilityMapper houseFacilityMapper;
    private final BannerMapper bannerMapper;
    private final CouponMapper couponMapper;
    private final CouponUserMapper couponUserMapper;
    private final CommentMapper commentMapper;
    private final MessageMapper messageMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userMapper.selectCount(null) > 0) {
            return;
        }
        User admin = saveUser("admin", "平台管理员", "13800000000", "ADMIN");
        User host = saveUser("host", "海岛房东", "13800000002", "HOST");
        User guest = saveUser("staruser", "星栖旅人", "13800000001", "USER");

        House seaside = saveHouse(host.getId(), "海边日落·精品民宿", "面向太平洋的无边日落，白沙与木廊交织。",
                "东京", "江之岛海岸 18 号", new BigDecimal("568.00"), "WHOLE", 4, 2, 2, 2, 4.9, 28, 96);
        House onsen = saveHouse(host.getId(), "京都町屋·温泉庭院", "百年町屋改造，庭院露天温泉与茶室。",
                "京都", "祇园白川通", new BigDecimal("498.00"), "WHOLE", 3, 1, 1, 2, 4.8, 19, 80);
        House snow = saveHouse(host.getId(), "北海道雪屋·星空木屋", "林间独立木屋，可观星与雪景温泉。",
                "北海道", "二世谷滑雪度假区", new BigDecimal("688.00"), "WHOLE", 5, 2, 2, 3, 4.7, 15, 72);
        House room = saveHouse(host.getId(), "冲绳蓝湾·海景套房", "酒店式独立套房，步行即达珊瑚沙滩。",
                "冲绳", "恩纳村海岸", new BigDecimal("428.00"), "HOTEL", 2, 1, 1, 1, 4.6, 22, 64);

        addImages(seaside.getId(), "seaside");
        addImages(onsen.getId(), "onsen");
        addImages(snow.getId(), "snow");
        addImages(room.getId(), "ocean");
        addFacilities(seaside.getId(), List.of("WiFi", "停车", "厨房", "空调", "泳池"));
        addFacilities(onsen.getId(), List.of("WiFi", "厨房", "空调", "早餐"));
        addFacilities(snow.getId(), List.of("WiFi", "停车", "厨房", "空调"));
        addFacilities(room.getId(), List.of("WiFi", "空调", "早餐", "泳池"));

        Comment comment = new Comment();
        comment.setUserId(guest.getId());
        comment.setHouseId(seaside.getId());
        comment.setOrderId(0L);
        comment.setScore(5);
        comment.setContent("日落把整面海面染成金色，晚上听得到潮声，会再来。");
        commentMapper.insert(comment);

        Banner banner = new Banner();
        banner.setTitle("发现更多美好旅程");
        banner.setSubtitle("探索海岛度假生活");
        banner.setImageUrl("https://picsum.photos/seed/starhaven-banner/900/480");
        banner.setLinkUrl("/pages/listing/listing");
        banner.setSort(1);
        banner.setStatus(1);
        bannerMapper.insert(banner);

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
        couponUser.setUserId(guest.getId());
        couponUser.setUsed(0);
        couponUserMapper.insert(couponUser);

        Message message = new Message();
        message.setUserId(guest.getId());
        message.setType("SYSTEM");
        message.setTitle("欢迎来到星栖");
        message.setContent("让每一次停留，都成为星光下的相遇。");
        message.setReadStatus(0);
        messageMapper.insert(message);

        log.info("演示数据已初始化 admin/host/staruser 密码均为 123456, adminId={}, hostId={}, guestId={}",
                admin.getId(), host.getId(), guest.getId());
    }

    private User saveUser(String username, String nickname, String phone, String role) {
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole(role);
        user.setStatus(1);
        user.setGender(0);
        user.setAvatar("https://picsum.photos/seed/" + username + "/200");
        userMapper.insert(user);
        return user;
    }

    private House saveHouse(Long hostId, String title, String desc, String city, String address,
                            BigDecimal price, String type, int guest, int room, int bath, int bed,
                            double score, int comments, int heat) {
        House house = new House();
        house.setHostId(hostId);
        house.setTitle(title);
        house.setCoverImage("https://picsum.photos/seed/" + city + "/800/600");
        house.setDescription(desc);
        house.setCity(city);
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
        house.setLatitude(new BigDecimal("35.658000"));
        house.setLongitude(new BigDecimal("139.701000"));
        houseMapper.insert(house);
        return house;
    }

    private void addImages(Long houseId, String seed) {
        for (int i = 1; i <= 3; i++) {
            HouseImage image = new HouseImage();
            image.setHouseId(houseId);
            image.setImageUrl("https://picsum.photos/seed/" + seed + i + "/900/600");
            image.setSort(i);
            houseImageMapper.insert(image);
        }
    }

    private void addFacilities(Long houseId, List<String> names) {
        for (String name : names) {
            HouseFacility facility = new HouseFacility();
            facility.setHouseId(houseId);
            facility.setFacilityName(name);
            houseFacilityMapper.insert(facility);
        }
    }
}
