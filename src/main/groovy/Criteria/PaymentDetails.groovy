package Criteria
/**
 * Created by ryabnat on 2/17/2016.
 */
class PaymentDetails {

    String externalId
    String details

    private PaymentDetails(Builder builder) {
        this.externalId = builder.externalId
        this.details = builder.details


    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        String externalId = ""
        String details = ""

        PaymentDetails build() {
            new PaymentDetails(this)
        }

        PaymentDetails.Builder setexternalid(String value) {
            if (value) {
                externalId = value
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