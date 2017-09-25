package Criteria

class CardDetails {

    String fullPAN
    String cardHolder

    private CardDetails(Builder builder) {
        this.fullPAN = builder.fullPAN
        this.cardHolder = builder.cardHolder


    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        String fullPAN = ""
        String cardHolder = ""

        CardDetails build() {
            new CardDetails(this)
        }

        CardDetails.Builder setfullPAN(String value) {
            if (value) {
                fullPAN = value
            }
            this
        }

        CardDetails.Builder setcardHolder(String value) {
            if (value) {
                cardHolder = value
            }
            this
        }



    }
}