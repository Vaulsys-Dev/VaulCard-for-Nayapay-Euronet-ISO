package vaulsys.webservice.walletcardmgmtwebservice.entity;


import javax.xml.bind.annotation.XmlRootElement;

/**
 * Asim Shahzad, Date : 6th June 2023, Tracking ID : VP-NAP-202303091
 */
//m.rehman: 04-12-2023, NAP-P7-23 - Switch-middleware integration v 5.1.9 (Cash Withdrawal Limit for L1)

@XmlRootElement
public class NPMWError {

    private String httpstatuscode;
    private String nayapaystatuscode;
    private String error;

    public String getHttpstatuscode() {
        return httpstatuscode;
    }

    public void setHttpstatuscode(String httpstatuscode) {
        this.httpstatuscode = httpstatuscode;
    }

    public String getNayapaystatuscode() {
        return nayapaystatuscode;
    }

    public void setNayapaystatuscode(String nayapaystatuscode) {
        this.nayapaystatuscode = nayapaystatuscode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "YourClassName{" +
                // ... (previous fields)
                "httpstatuscode='" + httpstatuscode + '\'' +
                ", nayapaystatuscode='" + nayapaystatuscode + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}

