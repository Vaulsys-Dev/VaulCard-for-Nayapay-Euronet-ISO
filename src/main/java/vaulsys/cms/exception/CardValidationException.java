package vaulsys.cms.exception;

import vaulsys.exception.base.DecisionMakerException;
import vaulsys.exception.impl.DecisionMakerExceptionImp;
import vaulsys.protocols.PaymentSchemes.base.ISOResponseCodes;
import vaulsys.protocols.ifx.imp.Ifx;
import vaulsys.util.Util;

/**
 * Created by Raza
 */
public class CardValidationException extends DecisionMakerExceptionImp {
    private Boolean returnError;

    public CardValidationException(String s) {
        super(s);
        //m.rehman
        //this.returnError = false;
        this.returnError = true;
    }

    public CardValidationException() {
        super();
        //m.rehman
        //this.returnError = false;
        this.returnError = true;
    }

    public CardValidationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        //m.rehman
        //this.returnError = false;
        this.returnError = true;
    }

    public CardValidationException(Throwable arg0) {
        super(arg0);
        //this.returnError = false;
        this.returnError = true;
    }

    public CardValidationException(String s, Boolean returnError) {
        super(s);
        this.returnError = returnError;
    }

    public CardValidationException(Boolean returnError) {
        super();
        this.returnError = returnError;
    }

    public CardValidationException(String arg0, Throwable arg1, Boolean returnError) {
        super(arg0, arg1);
        this.returnError = returnError;
    }

    public CardValidationException(Throwable arg0, Boolean returnError) {
        super(arg0);
        this.returnError = returnError;
    }

    public void alterIfxByErrorType(Ifx ifx) {
        if (getCause()!= null && getCause() instanceof DecisionMakerException){
            DecisionMakerException cause = (DecisionMakerException) getCause();
            cause.alterIfxByErrorType(ifx);
        }
        else if (!Util.hasText(ifx.getRsCode())) {
            ifx.setRsCode(ISOResponseCodes.LIMIT_EXCEEDED); //01
        }
    }

    @Override
    public boolean returnError() {
        return this.returnError ;
    }

}