package vaulsys.webservice.walletcardmgmtwebservice.entity;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
//m.rehman: 04-12-2023, NAP-P7-23 - Switch-middleware integration v 5.1.9 (Cash Withdrawal Limit for L1)

@XmlRootElement
public class NPMWResponseData {

    private String nayapayId;

    private String userId;

    //m.rehman: Euronet Integration
    private String stepupType;

    private String stepupRequestId;

    private String verificationToken;

    private String acquirerId;

    private String merchantId;

    private String merchantName;

    private String merchantURL;

    private String merchantCategoryCode;

    private String merchantCountryCode;

    private String transactionTimeStamp;

    private String transactionAmount;

    private String transactionCurrency;

    private String transactionExponent;

    private String remonustranxcounts;

    private String otp;

    public String getNayapayId() {
        return nayapayId;
    }

    public void setNayapayId(String nayapayId) {
        this.nayapayId = nayapayId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    //m.rehman: Euronet Integration
    public String getStepupType() {
        return stepupType;
    }

    public void setStepupType(String stepupType) {
        this.stepupType = stepupType;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getStepupRequestId() {
        return stepupRequestId;
    }

    public void setStepupRequestId(String stepupRequestId) {
        this.stepupRequestId = stepupRequestId;
    }

    public String getAcquirerId() {
        return acquirerId;
    }

    public void setAcquirerId(String acquirerId) {
        this.acquirerId = acquirerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantURL() {
        return merchantURL;
    }

    public void setMerchantURL(String merchantURL) {
        this.merchantURL = merchantURL;
    }

    public String getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    public void setMerchantCategoryCode(String merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    public String getMerchantCountryCode() {
        return merchantCountryCode;
    }

    public void setMerchantCountryCode(String merchantCountryCode) {
        this.merchantCountryCode = merchantCountryCode;
    }

    public String getTransactionTimeStamp() {
        return transactionTimeStamp;
    }

    public void setTransactionTimeStamp(String transactionTimeStamp) {
        this.transactionTimeStamp = transactionTimeStamp;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public String getTransactionExponent() {
        return transactionExponent;
    }

    public void setTransactionExponent(String transactionExponent) {
        this.transactionExponent = transactionExponent;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }


    // Asim Shahzad, Date : 6th June 2023, Tracking ID : VP-NAP-202303091
    private String bankcharges;
    private Boolean isonustranx;
    private String nayapaycharges;
    private String nayapaytaxamount;
    private String onustranxcounts;
    private String onusbalinqcounts;
    private String onusreceiptcounts;

    // Huzaifa: 11/08/2023: FW: NAP-P5-23 ==> [ Logging email ] ==> Segregation of ATM On Us Channels Bank - UBL & BAFL
    private String receiptcharges;

    private Boolean isreceiptreq;

    private String respcode;

    public String getRespcode() {
        return respcode;
    }

    public void setRespcode(String respcode) {
        this.respcode = respcode;
    }

    public String getBankcharges() {
        return bankcharges;
    }

    public void setBankcharges(String bankcharges) {
        this.bankcharges = bankcharges;
    }

    public Boolean getIsonustranx() {
        return isonustranx;
    }

    public void setIsonustranx(Boolean isonustranx) {
        this.isonustranx = isonustranx;
    }

    public String getNayapaycharges() {
        return nayapaycharges;
    }

    public void setNayapaycharges(String nayapaycharges) {
        this.nayapaycharges = nayapaycharges;
    }

    public String getNayapaytaxamount() {
        return nayapaytaxamount;
    }

    public void setNayapaytaxamount(String nayapaytaxamount) {
        this.nayapaytaxamount = nayapaytaxamount;
    }


    public String getReceiptcharges() {
        return receiptcharges;
    }

    public void setReceiptcharges(String receiptcharges) {
        this.receiptcharges = receiptcharges;
    }

    public Boolean getIsreceiptreq() {
        return isreceiptreq;
    }

    public void setIsreceiptreq(Boolean isreceiptreq) {
        this.isreceiptreq = isreceiptreq;
    }

    public String getOnustranxcounts() {
        return onustranxcounts;
    }

    public void setOnustranxcounts(String onustranxcounts) {
        this.onustranxcounts = onustranxcounts;
    }

    public String getOnusbalinqcounts() {
        return onusbalinqcounts;
    }

    public void setOnusbalinqcounts(String onusbalinqcounts) {
        this.onusbalinqcounts = onusbalinqcounts;
    }

    public String getOnusreceiptcounts() {
        return onusreceiptcounts;
    }

    public void setOnusreceiptcounts(String onusreceiptcounts) {
        this.onusreceiptcounts = onusreceiptcounts;
    }

    public String getRemonustranxcounts() {
        return remonustranxcounts;
    }

    public void setRemonustranxcounts(String remonustranxcounts) {
        this.remonustranxcounts = remonustranxcounts;
    }

    // ==================================================================

    @Override
    public String toString() {
        return "YourClassName{" +
                "nayapayId='" + nayapayId + '\'' +
                ", userId='" + userId + '\'' +
                ", stepupType='" + stepupType + '\'' +
                ", stepupRequestId='" + stepupRequestId + '\'' +
                ", verificationToken='" + verificationToken + '\'' +
                ", acquirerId='" + acquirerId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", merchantURL='" + merchantURL + '\'' +
                ", merchantCategoryCode='" + merchantCategoryCode + '\'' +
                ", merchantCountryCode='" + merchantCountryCode + '\'' +
                ", transactionTimeStamp='" + transactionTimeStamp + '\'' +
                ", transactionAmount='" + transactionAmount + '\'' +
                ", transactionCurrency='" + transactionCurrency + '\'' +
                ", transactionExponent='" + transactionExponent + '\'' +
                ", otp='" + otp + '\'' +
                ", bankcharges='" + bankcharges + '\'' +
                ", isonustranx=" + isonustranx +
                ", nayapaycharges='" + nayapaycharges + '\'' +
                ", nayapaytaxamount='" + nayapaytaxamount + '\'' +
                ", receiptcharges='" + receiptcharges + '\'' +
                ", respcode='" + respcode + '\'' +
                ", isreceiptreq=" + isreceiptreq +
                '}';
    }
}

