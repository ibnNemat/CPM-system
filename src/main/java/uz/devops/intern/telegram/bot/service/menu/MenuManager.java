package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class MenuManager extends BotStrategyAbs {

    private final String STATE = "MANAGER_MENU";
    private final Integer STEP = 4;

    @Autowired
    private List<ManagerMenuAbs> impls;
    private HashMap<String, ManagerMenuStrategy> menu;

    private final CustomerTelegramService customerTelegramService;
    private final TelegramGroupService telegramGroupService;
    private final UserService userService;

    public MenuManager(CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
        this.customerTelegramService = customerTelegramService;
        this.telegramGroupService = telegramGroupService;
        this.userService = userService;
    }

    @PostConstruct
    public void inject(){
        menu = new HashMap<>();
        for(ManagerMenuStrategy m: impls){
            List<String> texts = m.getSupportedTexts();
            for(String text: texts){
                menu.put(text, m);
            }
        }
        log.info("Inner classes are injected, Map size: {} | List: {} | Map: {}",
            menu.size(), impls, menu);
    }

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasMessage() && !update.getMessage().hasText()){
            Long userId = update.hasCallbackQuery()? update.getCallbackQuery().getFrom().getId() : null;
            wrongValue(userId, bundle.getString("bot.admin.send.only.message.or.contact"));
            log.warn("User didn't send text! User id: {} | Update: {}", userId, update);
            return false;
        }

        Long userId = update.getMessage().getFrom().getId();
        String messageText = update.getMessage().getText();

        ManagerMenuStrategy obj = menu.get(messageText);
        return obj.todo(update, manager);
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

//    @Service
//    public class Organization implements ManagerMenuStrategy{
//        private final String SUPPORTED_TEXT = "\uD83C\uDFE2 Yangi tashkilot";
//
//        public boolean todo(Update update, CustomerTelegramDTO manager){
//            String newMessage = "Iltimos tashkilot nomini kiriting";
//            SendMessage sendMessage = sendMessage(manager.getTelegramId(), newMessage);
//            adminFeign.sendMessage(sendMessage);
//            log.info("Admin is creating new organization, Manager id: {} | Message text: {} | Update: {}",
//                manager.getTelegramId(), update.getMessage().getText(), update);
//            manager.setStep(5);
//            return true;
//        }
//
//        @Override
//        public String getSupportedText() {
//            return SUPPORTED_TEXT;
//        }
//    }
//
//    @Service
//    @RequiredArgsConstructor
//    public class Group implements ManagerMenuStrategy{
//        private final String SUPPORTED_TEXT = "\uD83D\uDC65 Guruh qo'shish";
//
//        private final GroupsService groupsService;
//
//        @Override
//        public boolean todo(Update update, CustomerTelegramDTO manager) {
////            CustomerTelegramDTO manager = customerTelegramService.findByTelegramId(manager.getTelegramId());
//            ResponseDTO<User> responseDTO = userService.getUserByPhoneNumber(manager.getPhoneNumber());
//            if(!responseDTO.getSuccess() && responseDTO.getResponseData() == null){
//                log.warn("Manager is not found from jhi_user! Manager: {} | Response: {}", manager, responseDTO);
//                return false;
//            }
//
//            setUserToContextHolder(responseDTO.getResponseData());
//
//            List<TelegramGroupDTO> telegramGroups = manager.getTelegramGroups().stream().toList();
//            List<GroupsDTO> managerGroups = groupsService.findOnlyManagerGroups();
//
//            log.info("Manager telegram groups, Telegram groups count: {} | Telegram groups: {}", telegramGroups.size(), telegramGroups);
//            for(GroupsDTO group: managerGroups){
//                for(TelegramGroupDTO telegramGroup: telegramGroups){
//                    if(group.getName().equals(telegramGroup.getName())){
//                        telegramGroups.remove(telegramGroup);
//                    }
//                }
//            }
//            log.info("Manager telegram groups, Telegram groups count: {} | Telegram groups: {}", telegramGroups.size(), telegramGroups);
//
//            if(telegramGroups.isEmpty()){
//                wrongValue(manager.getId(), "Telegram guruhlar mavjud emas!");
//                log.warn("Has no telegram group, Manager id: {} ", manager.getTelegramId());
//                return false;
//            }
//
//            for(TelegramGroupDTO dto: telegramGroups){
//                String newMessage = createGroupText(dto);
//                InlineKeyboardMarkup markup = createGroupButtons(dto.getChatId());
//                SendMessage sendMessage =
//                    TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
//                adminFeign.sendMessage(sendMessage);
//            }
//            manager.setStep(6);
//            return true;
//        }
//
//        @Override
//        public String getSupportedText() {
//            return SUPPORTED_TEXT;
//        }
//
//        private String createGroupText(TelegramGroupDTO groupDTO){
//            ResponseDTO<List<CustomerTelegramDTO>> response =
//                customerTelegramService.getCustomerTgByChatId(groupDTO.getChatId());
//
//            StringBuilder text = new StringBuilder(String.format(
//                "Guruh nomi: %s\nFoydalanuvchilar soni: %d\n=========================\n",
//                groupDTO.getName(), response.getResponseData().size()
//            ));
//
//            List<CustomerTelegramDTO> customerTelegrams = response.getResponseData();
//            int index = 1;
//            for(CustomerTelegramDTO customer: customerTelegrams){
//                text.append(String.format(
//                    "%d. %s\n", index++, customer.getFirstname() + customer.getLastname()
//                ));
//            }
//
//            return text.toString();
//        }
//
//        private InlineKeyboardMarkup createGroupButtons(Long chatId){
//            List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
//
//            InlineKeyboardButton button = new InlineKeyboardButton("Shu guruhni qo'shish");
//            button.setCallbackData(String.valueOf(chatId));
//
//            buttons.add(
//                List.of(button)
//            );
//
//            return new InlineKeyboardMarkup(buttons);
//        }
//    }
//
//    @Service
//    public class Services implements ManagerMenuStrategy{
//        private final String SUPPORTED_TEXT = "\uD83E\uDEC2 Xizmat qo'shish";
//
//        @Override
//        public boolean todo(Update update, CustomerTelegramDTO manager) {
//            return false;
//        }
//
//        @Override
//        public String getSupportedText() {
//            return SUPPORTED_TEXT;
//        }
//    }
//
//    @Service
//    public class Payments implements ManagerMenuStrategy{
//        private final String SUPPORTED_TEXT = "\uD83D\uDCB8 Bolalar qarzdorliklari";
//
//        @Autowired
//        private PaymentService paymentService;
//
//        @Override
//        public boolean todo(Update update, CustomerTelegramDTO manager) {
//            if(!update.hasMessage() && !update.getMessage().hasText()){
//                Long userId = update.hasCallbackQuery()? update.getCallbackQuery().getFrom().getId() : null;
//                messageHasNotText(userId, update);
//                log.warn("User didn't send text! User id: {} | Update: {}", userId, update);
//                return false;
//            }
//
//            Long managerId = update.getMessage().getFrom().getId();
//
//            ResponseDTO<User> response = userService.getUserByPhoneNumber(manager.getPhoneNumber());
//            if(!response.getSuccess()){
//                wrongValue(managerId, response.getMessage());
//                log.warn("{} | Manager id: {} | Response: {}", response.getMessage(), managerId, response);
//                return false;
//            }
//            setUserToContextHolder(response.getResponseData());
//
//            List<PaymentDTO> payments = paymentService.getAllPaymentsCreatedByGroupManager();
//            if(payments.isEmpty()){
//                wrongValue(managerId, "Hozircha sizda qarzdorliklar yo'q");
//                log.warn("Payments list is empty, Manager id: {} ", managerId);
//                return false;
//            }
//
//            StringBuilder newMessage = new StringBuilder();
//            for(PaymentDTO payment: payments){
//                newMessage.append(payment + "\n\n");
//            }
//
//            SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage.toString());
//            adminFeign.sendMessage(sendMessage);
//            return true;
//        }
//
//        @Override
//        public String getSupportedText() {
//            return SUPPORTED_TEXT;
//        }
//    }
//
//    @Service
//    public class AllGroups implements ManagerMenuStrategy{
//        private final String SUPPORTED_TEXT = "\uD83D\uDC40 Guruhlarni ko'rish";
//
//        @Autowired
//        private GroupsService groupsService;
//        @Override
//        public boolean todo(Update update, CustomerTelegramDTO manager) {
//            ResponseDTO<User> responseDTO = userService.getUserByPhoneNumber(manager.getPhoneNumber());
//            if(!responseDTO.getSuccess()){
//                wrongValue(manager.getTelegramId(), responseDTO.getMessage());
//                log.warn("{} | Manager id: {} | Response: {}", responseDTO.getMessage(), manager.getTelegramId(), responseDTO);
//                return false;
//            }
//
//            setUserToContextHolder(responseDTO.getResponseData());
//
//            List<GroupsDTO> groups = groupsService.findOnlyManagerGroups();
//            if(groups.isEmpty()){
//                wrongValue(manager.getTelegramId(), "Tashkilotlarga biriktirilgan tashkilotlar hozircha mavjud emas!");
//                log.warn("Manager has not any groups! Manager: {}", manager);
//                return false;
//            }
//
//            StringBuilder newMessage = new StringBuilder();
//            for(GroupsDTO group: groups){
//                newMessage.append(String.format(
//                    "Guruh nomi: %s\n" +
//                        "Tashkilot nomi: %s\n" +
//                        "Foydalanuvchilar soni: %d\n",
//                    group.getName(), group.getOrganization().getName(), group.getCustomers().size()
//                ));
//            }
//
//            SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage.toString());
//            adminFeign.sendMessage(sendMessage);
//            return true;
//        }
//
//        @Override
//        public String getSupportedText() {
//            return SUPPORTED_TEXT;
//        }
//    }
//
//    @Service
//    public class NewBot implements ManagerMenuStrategy{
//        private final String SUPPORTED_TEXT = "";
//
//        @Override
//        public boolean todo(Update update, CustomerTelegramDTO manager) {
//            return false;
//        }
//
//        @Override
//        public String getSupportedText() {
//            return SUPPORTED_TEXT;
//        }
//    }
//
//    private void setUserToContextHolder(User user){
//        Set<GrantedAuthority> authorities =
//            user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getName())).collect(Collectors.toSet());
//
//        org.springframework.security.core.userdetails.User principal =
//            new org.springframework.security.core.userdetails.User(user.getLogin(),
//                user.getPassword() == null? "": user.getPassword(), authorities);
//
//        UsernamePasswordAuthenticationToken authentication =
//            new UsernamePasswordAuthenticationToken(principal, null, authorities);
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }

}
