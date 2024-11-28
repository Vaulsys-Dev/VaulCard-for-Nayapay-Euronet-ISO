package vaulsys.cms.base;

import org.apache.log4j.LogManager;
import vaulsys.persistence.GeneralDao;
import vaulsys.persistence.IEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import vaulsys.protocols.ifx.enums.AccType;
import vaulsys.util.WebServiceUtil;
import org.apache.log4j.Logger;
import vaulsys.webservice.walletcardmgmtwebservice.entity.WalletCMSWsEntity;

import javax.persistence.*;
import java.util.*;

/**
 * Created by HP on 4/21/2017.
 */
@Entity
@Table(name="cms_card")
public class CMSCard implements IEntity<Long> {

    private static final Logger logger = LogManager.getLogger(CMSCard.class);

    @Id
    @GeneratedValue(generator="CMS_CARD_ID_SEQ-gen")
    @org.hibernate.annotations.GenericGenerator(name = "CMS_CARD_ID_SEQ-gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "CMS_CARD_ID_SEQ")
            })
    private Long id;

    @Column(name="card_number")
    private String cardNumber;

    @Column(name="card_name")
    private String cardName;

    @Column(name="REQUEST_DATE")
    private String requestDate;

    @Column(name="create_date")
    private String createDate;

    @Column(name="activation_date")
    private String activationDate;

    @Column(name="expiry_date")
    private String expiryDate;

    @Column(name="delivery_date")
    private String deliveryDate;

    @Column(name="cardnumber_expiry_relation")
    private String cardNumberExpiryRelation;

    @Column(name="is_supplementary")
    private boolean isSupplementary;

    @Column(name="card_status")
    private String cardStatus;

    @Column(name="branch")
    String branch;
    //Branch branch; //Raza commenting not using Branch Object

    @Column(name="primary_card_number")
    private String primaryCardNumber;

    @Column(name="last_card_number")
    private String lastCardNumber;

//    @Column(name="CUSTOMER_ID")
//    private String customerID;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    @Cascade(value = org.hibernate.annotations.CascadeType.ALL )
    @ForeignKey(name="CMSACCOUNT_CUST_FK")
    private CMSCustomer customer;

// Asim Shahzad, Date : 21st Jan 2021, Tracking ID : VC-NAP-202101071 / VP-NAP-202101071 / VG-NAP-202101071 (Release # 5)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCT_ID", insertable = true, updatable = true)
    @Cascade(value = org.hibernate.annotations.CascadeType.ALL )
    @ForeignKey(name="CMS_CARD_CMS_PRODUCT_FK1")
    private CMSProduct product;

    @Transient
    private List<CMSProduct> list_CustProducts;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    private Set<CMSAccount> list_CustAccounts;

    @Column(name = "CARDNOLASTDIGITS")
    private String cardNoLastDigits;

    //m.rehman: 11-08-2020: saving tracking id for card embossing file
    @Column(name = "TRACKING_ID")
    private String trackingId;

    //Arsalan Akhter, Date: 12_March_2021, Tracking ID: VP-NAP-202103114_VC-NAP-202103114
    @Column(name="IS_EXPORTED")
    private String isExported;
    //*************************************


    //*************************************

    //Added by m.waleed 06/09/20233 VC-NAP-202303062 - Merchant Portal
    @Column(name="CARD_ID")
    private String cardid;

    @Column(name="EMPLOYEE_ID")
    private String employeeid;

    @Column(name="BUSINESS_NAME_ON_CARD")
    private String businessName;

    //Added For Card Delivery
    @Column(name="PHONE_NUMBER")
    private String phoneNumber;

    @Column(name="DELIVERY_ADDRESS")
    private String deliveryAddress;

    @Column(name="DELIVERY_CITY")
    private String deliveryCity;


    //================================================================


    public CMSCard()
    {}

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String importDate) {
        this.requestDate = importDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getCardNumberExpiryRelation() {
        return cardNumberExpiryRelation;
    }

    public void setCardNumberExpiryRelation(String cardNumberExpiryRelation) {
        this.cardNumberExpiryRelation = cardNumberExpiryRelation;
    }

    public boolean getSupplementary() {
        return isSupplementary;
    }

    public void setSupplementary(boolean supplementary) {
        isSupplementary = supplementary;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getPrimaryCardNumber() {
        return primaryCardNumber;
    }

    public void setPrimaryCardNumber(String primaryCardNumber) {
        this.primaryCardNumber = primaryCardNumber;
    }

    public String getLastCardNumber() {
        return lastCardNumber;
    }

    public void setLastCardNumber(String lastCardNumber) {
        this.lastCardNumber = lastCardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public CMSProduct getProduct() {
        return product;
    }

    public void setProduct(CMSProduct product) {
        this.product = product;
    }

    public List<CMSProduct> getList_CustProducts() {
        return list_CustProducts;
    }

    public void setList_CustProducts(List<CMSProduct> list_CustProducts) {
        this.list_CustProducts = list_CustProducts;
    }

    public Set<CMSAccount> getList_CustAccounts() {
        return list_CustAccounts;
    }

    public Set<CMSAccount> getList_CustAccounts(CMSCustomer cmsCustomer) { //Waleed expilicty fetch Accounts when required..!

//        logger.info("WSGHere");
//        logger.info("Fname:"+cmsCustomer.getFirstname());
//        logger.info("Active check:"+GeneralDao.Instance.getCurrentSession().getTransaction().isActive());
        if(GeneralDao.Instance.getCurrentSession().getTransaction().isActive()){
            try{
                String dbQuery;
                Map<String, Object> params;
                dbQuery = "from " + CMSAccount.class.getName() + " c where c.customer = :CUST and c.category= :CAT ";
                params = new HashMap<String, Object>();
                params.put("CUST", cmsCustomer);
                params.put("CAT", AccType.CAT_MERCHANT_EXP_WALLET_VALUE);
                List<CMSAccount> lstAccounts = GeneralDao.Instance.find(dbQuery, params);
//                logger.info("Size:"+ lstAccounts.size());
                Set<CMSAccount> setAccounts = new HashSet<>(lstAccounts);
                return  setAccounts;
            }
            catch (Exception e){
                logger.error("Exception caught while getting getList_CustAccounts for this transaction...!");
                logger.error(WebServiceUtil.getStrException(e));
                return null;
            }
        }
        return  null;
    }

    public void setList_CustAccounts(Set<CMSAccount> list_CustAccounts) {
        this.list_CustAccounts = list_CustAccounts;
    }

    public CMSCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(CMSCustomer customer) {
        this.customer = customer;
    }

    public String getCardNoLastDigits() {
        return cardNoLastDigits;
    }

    public void setCardNoLastDigits(String cardNoLastDigits) {
        this.cardNoLastDigits = cardNoLastDigits;
    }

    //m.rehman: 11-08-2020: saving tracking id for card embossing file
    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    //Arsalan Akhter, Date: 12_March_2021, Tracking ID: VP-NAP-202103114_VC-NAP-202103114
    public String getIsExported() { return isExported; }

    public void setIsExported(String isExported) { this.isExported = isExported; }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(String employeeid) {
        this.employeeid = employeeid;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }
    //*************************************************************
}
