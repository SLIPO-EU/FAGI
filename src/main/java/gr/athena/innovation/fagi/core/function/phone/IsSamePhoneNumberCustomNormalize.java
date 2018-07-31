package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import gr.athena.innovation.fagi.core.function.IFunctionTwoStringParameters;

/**
 *
 * @author nkarag
 */
public class IsSamePhoneNumberCustomNormalize  implements IFunction, IFunctionTwoStringParameters{
    
    private static final org.apache.logging.log4j.Logger LOG 
            = LogManager.getLogger(IsSamePhoneNumberCustomNormalize.class);
    
    /**
     * Checks if two telephone numbers are the same using a custom normalization method.
     * 
     * @param phoneText1 The first phone number as text.
     * @param phoneText2 The second phone number as text.
     * @return True if the numbers are the same or extremely close and false otherwise.
     */
    @Override
    public boolean evaluate(String phoneText1, String phoneText2){

        if(StringUtils.isBlank(phoneText1) || StringUtils.isBlank(phoneText2)){
            return false;
        }
        
        if(phoneText1.equals(phoneText2)){
            return true;
        }

        PhoneNumber phone1 = createPhoneNumber(phoneText1);
        PhoneNumber phone2 = createPhoneNumber(phoneText2);

        String numerical1 = removeNonNumericCharacters(phoneText1);
        String numerical2 = removeNonNumericCharacters(phoneText2);

        if(numerical1.equals(numerical2)){
            return true;
        } else if(phone1.isUnknownFormat() || phone2.isUnknownFormat()){
            return false;
        }

        //both are known formats from now on
        if((phone1.hasCountryCode() && !phone2.hasCountryCode()) 
                || (!phone1.hasCountryCode() && phone2.hasCountryCode())
                || (!phone1.hasCountryCode() && !phone2.hasCountryCode())){ 

            //cannot compare using exit codes. Continue with area code, line number and internal code.
            if(phone1.getAreaCode().equals(phone2.getAreaCode())){

                if(!phone1.getLineNumber().equals(phone2.getLineNumber())){
                    if(phone1.getLineNumber().equals(phone2.getLineNumber()+phone2.getInternal())){
                        //phone1 line number contains internal digits of phone2
                        return true;
                    } else if(phone2.getLineNumber().equals(phone1.getLineNumber()+phone1.getInternal())){
                        //phone2 line number contains internal digits of phone1
                        return true;
                    } else {
                        //check again, ignoring the trailing zero from line number
                        if(phone1.getLineNumber().endsWith("0")){
                            String line1 = phone1.getLineNumber().substring(0, phone1.getLineNumber().length()-1);
                            if(line1.equals(phone2.getLineNumber())){
                                return true;
                            }
                        } else if(phone2.getLineNumber().endsWith("0")){
                            String line2 = phone2.getLineNumber().substring(0, phone2.getLineNumber().length()-1);
                            if(line2.equals(phone1.getLineNumber())){
                                return true;
                            }                            
                        }
                    }
                } else if(phone1.getLineNumber().equals(phone2.getLineNumber())){
                    
                    return phone1.getLineNumber().endsWith(phone2.getInternal()) 
                            || phone2.getLineNumber().endsWith(phone1.getInternal());
                    
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            if(phone1.getCountryCode().equals(phone2.getCountryCode())){
                if(phone1.getAreaCode().equals(phone2.getAreaCode())){
                    if(phone1.getLineNumber().equals(phone2.getLineNumber())){
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }    

    private boolean recognizeExitCodeDigits(String number) {

        if(number.indexOf('+') == 0){
            if(number.indexOf("(") == 1 && number.indexOf(")") == 4){
                return true;
            }
        }
        return false;
    }

    private String removeExitCode(String phoneNumber) {
        String result = phoneNumber.substring(4);
        return result;
    }
    
    private String getExitCodeIfExists(String number) {
        if(number.indexOf('+') == 0){
            if(number.indexOf("(") == 1 && number.indexOf(")") == 4){
                return number.substring(0, 4);
            } else {
                return number.substring(0, 2);
            }
        } else {
            return null;
        }
    }
    
    private String getAreaCodeIfExists(String number) {
        if(number.indexOf('+') == 0){
            if(number.indexOf("(") == 1 && number.indexOf(")") == 4){
                return number.substring(0, 4);
            } else {
                return number.substring(0, 2);
            }
        } else {
            return null;
        }
    }

    private PhoneNumber createPhoneNumber(String number){

        PhoneNumber phoneNumber = new PhoneNumber();

        //set + and country code
        if(number.indexOf('+') == 0){
            
            phoneNumber.setHasPlus(true);
            if(number.indexOf("(") == 1 && number.indexOf(")") == 4){
                phoneNumber.setCountryCode(number.substring(2, 4));
            } else {
                phoneNumber.setCountryCode(number.substring(1, 3));
            }
            phoneNumber.setHasCountryCode(true);
        } else {
            phoneNumber.setHasPlus(false);
            if(number.indexOf("(") == 0 && number.indexOf(")") == 3){
                phoneNumber.setCountryCode(number.substring(1, 2));
                phoneNumber.setHasCountryCode(true);
            } else {
                phoneNumber.setCountryCode(null);
                phoneNumber.setHasCountryCode(false);
            }
        }

        //set area code
        if(phoneNumber.getCountryCode() != null){

            String[] codes = StringUtils.substringsBetween(number, "(", ")");
            if(codes != null){
                phoneNumber.setAreaCode(codes[1]);
            }
        } else {
            if(number.contains("/")){
                String[] parts = number.split("/");
                
                if(parts[0].startsWith("0")){ //found zero prefix, removing it
                    String ar = StringUtils.right(parts[0], parts[0].length()-1);
                    phoneNumber.setAreaCode(ar);
                } else {
                    phoneNumber.setUnknownFormat(true);
                    phoneNumber.setNumericalValue(removeNonNumericCharacters(number));
                    phoneNumber.setLineNumber(number);
                }
            } else {
                phoneNumber.setUnknownFormat(true);
                phoneNumber.setNumericalValue(removeNonNumericCharacters(number));
                phoneNumber.setLineNumber(number);
            }
        }
        
        //set line number
        if(number.contains("/")){
            String[] parts = number.split("/");
            
            String lineNumber = removeNonNumericCharacters(parts[1]);
            phoneNumber.setLineNumber(lineNumber);
            
            if(parts[1].contains("-")){
                String[] digits = parts[1].split("-");
                phoneNumber.setInternal(digits[1]);
            } else {
                phoneNumber.setInternal("");
            }
        } else if(number.contains("-")){
            int index = number.indexOf("-", number.indexOf("-") + 1);
            String lineNumber = number.substring(index);
            phoneNumber.setLineNumber(removeNonNumericCharacters(lineNumber));
            
        } else {
            phoneNumber.setUnknownFormat(true);
            phoneNumber.setNumericalValue(removeNonNumericCharacters(number));
        }

        return phoneNumber;
    }
    
    private static String removeNonNumericCharacters(String phone){
        
        String phoneNumerical = phone.replaceAll("[^0-9]", "");
        
        return phoneNumerical;
    }    
}
