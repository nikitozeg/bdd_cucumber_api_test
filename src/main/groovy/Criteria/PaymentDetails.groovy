package Criteria
/**
 * Created by ryabnat on 2/17/2016.
 */
class PaymentDetails {

    String externalid
    String details

    private PaymentDetails(Builder builder) {
        this.externalid = builder.externalid
        this.details = builder.details


    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        String externalid = ""
        String details = ""

        PaymentDetails build() {
            new PaymentDetails(this)
        }

        PaymentDetails.Builder setexternalid(String value) {
            if (value) {
                externalid = value
            }
            this
        }

        PaymentDetails.Builder setdetails(String value) {
            if (value) {
                details = value
            }
            this
        }



    }
}