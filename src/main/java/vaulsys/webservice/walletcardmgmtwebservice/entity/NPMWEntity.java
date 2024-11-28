package vaulsys.webservice.walletcardmgmtwebservice.entity;

import vaulsys.webservice.walletcardmgmtwebservice.model.CardObject;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
//m.rehman: 04-12-2023, NAP-P7-23 - Switch-middleware integration v 5.1.9 (Cash Withdrawal Limit for L1)

@XmlRootElement
public class NPMWEntity {
    private String userid;
    private String cardnumber;
    private String cardtype;
    private String bankcode;
    private String transactiontype;
    private String istransactiononus;
    private String receiptflag;
    private String internationalflag;
    private String acquiringamount;
    private String acquiringcurrency;
    private String basepkramount;
    private String internationaltransactionfeeusd;
    private String internationaltransactionfeepkr;
    private String sstinternationaltransactionfee;
    private String fxsettlementcost;
    private String withholdingtax;
    private String totalamount;
    private String currentbalance;
    private String terminallocation;
    private String transactionref;
    private String posentrymode;
    private String merchantcategorycode;
    private String merchantname;
    private String cardlastdigit;
    private String stan;
    private String rrn;
    private String transdatetime;
    private String respcodeforswitch;
    private String respcodeforacquirer;
    private String isonuscharged;
    private String bankcharges;
    private String nayapaycharge;
    private String taxpercent;
    private String taxamount;
    private String isbalanceinqcharged;
    private String balanceinqcharges;
    private String isreceiptcharged;
    private String receiptcharges;
    private String onusoroffustransaction;
    private String transactioncount;
    private String responsecode;
    private String limitupdate;
    private String tranrefnumber;
    private String acctbalance;
    private Boolean isonusbanktranx;
    private String nayapaytrantype;
    private String remonustranxcounts;
    private Boolean success;

    private NPMWResponseData data;
    private List<CardObject> cardobjectlist;
    private List<NPMWError> errors;




    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getTransactiontype() {
        return transactiontype;
    }

    public void setTransactiontype(String transactiontype) {
        this.transactiontype = transactiontype;
    }

    public String getIstransactiononus() {
        return istransactiononus;
    }

    public void setIstransactiononus(String istransactiononus) {
        this.istransactiononus = istransactiononus;
    }

    public String getReceiptflag() {
        return receiptflag;
    }

    public void setReceiptflag(String receiptflag) {
        this.receiptflag = receiptflag;
    }

    public String getInternationalflag() {
        return internationalflag;
    }

    public void setInternationalflag(String internationalflag) {
        this.internationalflag = internationalflag;
    }

    public String getAcquiringamount() {
        return acquiringamount;
    }

    public void setAcquiringamount(String acquiringamount) {
        this.acquiringamount = acquiringamount;
    }

    public String getAcquiringcurrency() {
        return acquiringcurrency;
    }

    public void setAcquiringcurrency(String acquiringcurrency) {
        this.acquiringcurrency = acquiringcurrency;
    }

    public String getBasepkramount() {
        return basepkramount;
    }

    public void setBasepkramount(String basepkramount) {
        this.basepkramount = basepkramount;
    }

    public String getInternationaltransactionfeeusd() {
        return internationaltransactionfeeusd;
    }

    public void setInternationaltransactionfeeusd(String internationaltransactionfeeusd) {
        this.internationaltransactionfeeusd = internationaltransactionfeeusd;
    }

    public String getInternationaltransactionfeepkr() {
        return internationaltransactionfeepkr;
    }

    public void setInternationaltransactionfeepkr(String internationaltransactionfeepkr) {
        this.internationaltransactionfeepkr = internationaltransactionfeepkr;
    }

    public String getSstinternationaltransactionfee() {
        return sstinternationaltransactionfee;
    }

    public void setSstinternationaltransactionfee(String sstinternationaltransactionfee) {
        this.sstinternationaltransactionfee = sstinternationaltransactionfee;
    }

    public String getFxsettlementcost() {
        return fxsettlementcost;
    }

    public void setFxsettlementcost(String fxsettlementcost) {
        this.fxsettlementcost = fxsettlementcost;
    }

    public String getWithholdingtax() {
        return withholdingtax;
    }

    public void setWithholdingtax(String withholdingtax) {
        this.withholdingtax = withholdingtax;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public String getTerminallocation() {
        return terminallocation;
    }

    public void setTerminallocation(String terminallocation) {
        this.terminallocation = terminallocation;
    }

    public String getTransactionref() {
        return transactionref;
    }

    public void setTransactionref(String transactionref) {
        this.transactionref = transactionref;
    }

    public String getPosentrymode() {
        return posentrymode;
    }

    public void setPosentrymode(String posentrymode) {
        this.posentrymode = posentrymode;
    }

    public String getMerchantcategorycode() {
        return merchantcategorycode;
    }

    public void setMerchantcategorycode(String merchantcategorycode) {
        this.merchantcategorycode = merchantcategorycode;
    }

    public String getMerchantname() {
        return merchantname;
    }

    public void setMerchantname(String merchantname) {
        this.merchantname = merchantname;
    }

    public String getCardlastdigit() {
        return cardlastdigit;
    }

    public void setCardlastdigit(String cardlastdigit) {
        this.cardlastdigit = cardlastdigit;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getTransdatetime() {
        return transdatetime;
    }

    public void setTransdatetime(String transdatetime) {
        this.transdatetime = transdatetime;
    }

    public String getCurrentbalance() {
        return currentbalance;
    }

    public void setCurrentbalance(String currentbalance) {
        this.currentbalance = currentbalance;
    }

    public String getRespcodeforswitch() {
        return respcodeforswitch;
    }

    public void setRespcodeforswitch(String respcodeforswitch) {
        this.respcodeforswitch = respcodeforswitch;
    }

    public String getRespcodeforacquirer() {
        return respcodeforacquirer;
    }

    public void setRespcodeforacquirer(String respcodeforacquirer) {
        this.respcodeforacquirer = respcodeforacquirer;
    }

    public String getIsonuscharged() {
        return isonuscharged;
    }

    public void setIsonuscharged(String isonuscharged) {
        this.isonuscharged = isonuscharged;
    }

    public String getBankcharges() {
        return bankcharges;
    }

    public void setBankcharges(String bankcharges) {
        this.bankcharges = bankcharges;
    }

    public String getNayapaycharge() {
        return nayapaycharge;
    }

    public void setNayapaycharge(String nayapaycharge) {
        this.nayapaycharge = nayapaycharge;
    }

    public String getTaxpercent() {
        return taxpercent;
    }

    public void setTaxpercent(String taxpercent) {
        this.taxpercent = taxpercent;
    }

    public String getTaxamount() {
        return taxamount;
    }

    public void setTaxamount(String taxamount) {
        this.taxamount = taxamount;
    }

    public String getIsbalanceinqcharged() {
        return isbalanceinqcharged;
    }

    public void setIsbalanceinqcharged(String isbalanceinqcharged) {
        this.isbalanceinqcharged = isbalanceinqcharged;
    }

    public String getBalanceinqcharges() {
        return balanceinqcharges;
    }

    public void setBalanceinqcharges(String balanceinqcharges) {
        this.balanceinqcharges = balanceinqcharges;
    }

    public String getIsreceiptcharged() {
        return isreceiptcharged;
    }

    public void setIsreceiptcharged(String isreceiptcharged) {
        this.isreceiptcharged = isreceiptcharged;
    }

    public String getReceiptcharges() {
        return receiptcharges;
    }

    public void setReceiptcharges(String receiptcharges) {
        this.receiptcharges = receiptcharges;
    }

    public String getOnusoroffustransaction() {
        return onusoroffustransaction;
    }

    public void setOnusoroffustransaction(String onusoroffustransaction) {
        this.onusoroffustransaction = onusoroffustransaction;
    }

    public String getTransactioncount() {
        return transactioncount;
    }

    public void setTransactioncount(String transactioncount) {
        this.transactioncount = transactioncount;
    }

    public String getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(String responsecode) {
        this.responsecode = responsecode;
    }

    public String getLimitupdate() {
        return limitupdate;
    }

    public void setLimitupdate(String limitupdate) {
        this.limitupdate = limitupdate;
    }

    public String getTranrefnumber() {
        return tranrefnumber;
    }

    public void setTranrefnumber(String tranrefnumber) {
        this.tranrefnumber = tranrefnumber;
    }

    public String getAcctbalance() {
        return acctbalance;
    }

    public void setAcctbalance(String acctbalance) {
        this.acctbalance = acctbalance;
    }

    public Boolean getIsonusbanktranx() {
        return isonusbanktranx;
    }

    public void setIsonusbanktranx(Boolean isonusbanktranx) {
        this.isonusbanktranx = isonusbanktranx;
    }

    public String getNayapaytrantype() {
        return nayapaytrantype;
    }

    public void setNayapaytrantype(String nayapaytrantype) {
        this.nayapaytrantype = nayapaytrantype;
    }

    public String getRemonustranxcounts() {
        return remonustranxcounts;
    }

    public void setRemonustranxcounts(String remonustranxcounts) {
        this.remonustranxcounts = remonustranxcounts;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public NPMWResponseData getData() {
        return data;
    }

    public void setData(NPMWResponseData data) {
        this.data = data;
    }

    public List<CardObject> getCardobjectlist() {
        return cardobjectlist;
    }

    public void setCardobjectlist(List<CardObject> cardobjectlist) {
        this.cardobjectlist = cardobjectlist;
    }

    public List<NPMWError> getErrors() {
        return errors;
    }

    public void setErrors(List<NPMWError> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "TransactionDetails{" +
                "userid='" + userid + '\'' +
                ", cardnumber='" + cardnumber + '\'' +
                ", cardtype='" + cardtype + '\'' +
                ", bankcode='" + bankcode + '\'' +
                ", transactiontype='" + transactiontype + '\'' +
                ", istransactiononus='" + istransactiononus + '\'' +
                ", receiptflag='" + receiptflag + '\'' +
                ", internationalflag='" + internationalflag + '\'' +
                ", acquiringamount='" + acquiringamount + '\'' +
                ", acquiringcurrency='" + acquiringcurrency + '\'' +
                ", basepkramount='" + basepkramount + '\'' +
                ", internationaltransactionfeeusd='" + internationaltransactionfeeusd + '\'' +
                ", internationaltransactionfeepkr='" + internationaltransactionfeepkr + '\'' +
                ", sstinternationaltransactionfee='" + sstinternationaltransactionfee + '\'' +
                ", fxsettlementcost='" + fxsettlementcost + '\'' +
                ", withholdingtax='" + withholdingtax + '\'' +
                ", totalamount='" + totalamount + '\'' +
                ", currentbalance='" + currentbalance + '\'' +
                ", terminallocation='" + terminallocation + '\'' +
                ", transactionref='" + transactionref + '\'' +
                ", posentrymode='" + posentrymode + '\'' +
                ", merchantcategorycode='" + merchantcategorycode + '\'' +
                ", merchantname='" + merchantname + '\'' +
                ", cardlastdigit='" + cardlastdigit + '\'' +
                ", stan='" + stan + '\'' +
                ", rrn='" + rrn + '\'' +
                ", transdatetime='" + transdatetime + '\'' +
                ", respcodeforswitch='" + respcodeforswitch + '\'' +
                ", respcodeforacquirer='" + respcodeforacquirer + '\'' +
                ", isonuscharged='" + isonuscharged + '\'' +
                ", bankcharges='" + bankcharges + '\'' +
                ", nayapaycharge='" + nayapaycharge + '\'' +
                ", taxpercent='" + taxpercent + '\'' +
                ", taxamount='" + taxamount + '\'' +
                ", isbalanceinqcharged='" + isbalanceinqcharged + '\'' +
                ", balanceinqcharges='" + balanceinqcharges + '\'' +
                ", isreceiptcharged='" + isreceiptcharged + '\'' +
                ", receiptcharges='" + receiptcharges + '\'' +
                ", onusoroffustransaction='" + onusoroffustransaction + '\'' +
                ", transactioncount='" + transactioncount + '\'' +
                ", responsecode='" + responsecode + '\'' +
                ", limitupdate='" + limitupdate + '\'' +
                ", tranrefnumber='" + tranrefnumber + '\'' +
                ", acctbalance='" + acctbalance + '\'' +
                ", isonusbanktranx=" + isonusbanktranx +
                ", nayapaytrantype='" + nayapaytrantype + '\'' +
                ", remonustranxcounts='" + remonustranxcounts + '\'' +
                ", success=" + success +
                '}';
    }
}