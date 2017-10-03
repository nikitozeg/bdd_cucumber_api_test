@Token_for_bizSanction@qa.swiftcom.uk

Feature: EPA v2/fees test

  Background:
    # And Token service is disconnected
    And Fees is disconnected
    # And Token service is connected
  #  And Token recieved for user bizSanction@qa.swiftcom.uk

  Scenario Outline: Mass payment: Purse --> Card (validate by fullPAN) (single Payment)sdf
  #  And Token service is connected
   # And Token recieved for user bizSanction@qa.swiftcom.uk
    And Fees is connected

    And Add parameters to body request for payment service via "<requestPattern>" pattern by:
      | fromProvider | fromIdentity | fromCurrency   | fromAmount   | toProvider   | toCurrency   | toAmount   | details        | externalId |
      | eWaLLet      | 000-571041   | <fromCurrency> | <fromAmount> | <toProvider> | <toCurrency> | <toAmount> | some text here | *UUID*     |
    And Add card identity to body request for payment service via "<requestPattern>" pattern by:
      | fullPAN    | cardHolder    |
      | <fullPAN1> | <cardHolder1> |
    * Root object is:
      | state | dateStarted | dateFinished | payments       | id   | self | errorCode       | errorMsgs       |
      | New   | null        | null         | some text here | null | null | <rooterrorCode> | <rooterrorMsgs> |
    * Payment object is:
      | id                                   | to           | from    | externalId | details        | metadata | state  | dateStarted | dateFinished | quote | quoteExpireIn | rate | redirectUrl | errorCode   | errorMsgs   |
      | 00000000-0000-0000-0000-000000000000 | <toProvider> | eWaLLet | *UUID*     | some text here | null     | Failed | null        | null         | null  | null          | null | null        | <errorCode> | <errorMsgs> |
    * From object is:
      | provider | identity   | amount               | currency       | fee  |
      | eWaLLet  | 000-571041 | <receivedFromAmount> | <fromCurrency> | null |
    * To object is:
      | provider     | identity           | amount | currency     | fee  |
      | <toProvider> | <receivedIdentity> | null   | <toCurrency> | null |

    And Fees Request is executed
    And Fees results checked

    Examples: check validation for identity=fullPAN:
      | fromCurrency | fromAmount | --- | toProvider | toCurrency | toAmount | fullPAN1   | cardHolder1 | receivedFromAmount | receivedIdentity                      | errorCode | errorMsgs                                                | rooterrorCode | rooterrorMsgs                                                                      | requestPattern |
      | RUB          | 1000       | --- | card       | RUB        |          |            |             | 1000.0             | {}                                    | -4        | [Unfortunately this service is temporarily unavailable]  | 19055         | [Mass payment has not been processed completely. Please check transaction results] | absent         |
      | RUB          | 1000       | --- | card       | RUB        |          |            |             | 1000.0             | {cardHolder=null, fullPAN=null}       | -4        | [Unfortunately this service is temporarily unavailable]  | 19055         | [Mass payment has not been processed completely. Please check transaction results] | null           |
      | RUB          |            | --- | CARD       | RUB        |          |            |             | null               | {cardHolder=null, fullPAN=null}       | 53020     | [Only the amount to be sent or received must be entered] | 19055         | [Mass payment has not been processed completely. Please check transaction results] | null           |
      | RUB          | 1000       | --- | CARD       | RUB        |          | invalidPAN |             | 1000.0             | {cardHolder=null, fullPAN=invalidPAN} | 53003     | [Recipient entered incorrectly]                          | 19055         | [Mass payment has not been processed completely. Please check transaction results] | null           |

  ##    | eWaLLet      | 000-571041   | RUB          | 1000       | --- | card       | usd        |          | 1       |          |             | 1000.0             | {cardHolder=null, fullPAN=null} | 53003     | [Recipient entered incorrectly] | 0         | [] | Failed | null           |



  Scenario Outline: Mass payment: Purse --> Card (validate from/to objects) (single Payment)

    And Fees is connected

    And Add parameters to body request for payment service via "<requestPattern>" pattern by:
      | fromProvider | fromIdentity | fromCurrency   | fromAmount   | toProvider | toCurrency   | toAmount   | details        | externalId |
      | eWaLLet      | 000-571041   | <fromCurrency> | <fromAmount> | card       | <toCurrency> | <toAmount> | some text here | *UUID*     |
    And Add card identity to body request for payment service via "<requestPattern>" pattern by:
      | fullPAN          | cardHolder |
      | 4483150610545551 | cardHolder |

    * Root object is:
      | state | dateStarted | dateFinished | payments       | id   | self | errorCode       | errorMsgs       |
      | New   | null        | null         | some text here | null | null | <rooterrorCode> | <rooterrorMsgs> |
    * Payment object is:
      | id                                   | to   | from    | externalId | details        | metadata | state  | dateStarted | dateFinished | quote | quoteExpireIn | rate | redirectUrl | errorCode   | errorMsgs   |
      | 00000000-0000-0000-0000-000000000000 | card | eWaLLet | *UUID*     | some text here | null     | Failed | null        | null         | null  | null          | null | null        | <errorCode> | <errorMsgs> |

    * From object is:
      | provider | identity   | amount               | currency               | fee  |
      | eWaLLet  | 000-571041 | <receivedFromAmount> | <receivedFromCurrency> | null |

    * To object is:
      | provider | identity                                          | amount             | currency             | fee  |
      | card     | {cardHolder=cardHolder, fullPAN=4483150610545551} | <receivedToAmount> | <receivedToCurrency> | null |

    And Fees Request is executed
    And Fees results checked



  #  Examples: check validation for fromCurrency :
   #   | fromCurrency | fromAmount | --- | toCurrency | toAmount | --- | receivedFromCurrency | receivedFromAmount | receivedToCurrency | receivedToAmount | errorCode | errorMsgs | rooterrorCode | rooterrorMsgs | requestPattern |
   #   |              | 1000       | --- |  usd         | null     | --- |                      | 1000.0             |                    | null             | 53008     | [Currency to be sent entered incorrectly] | 19055         | [Mass payment has not been processed completely. Please check transaction results] | empty          |
   #   |              | 1000       | --- |  usd          | null     | --- | null                 | 1000.0             | null               | null             | 53008     | [Currency to be sent entered incorrectly] | 19055         | [Mass payment has not been processed completely. Please check transaction results] | null           |
    #  |              | 1000       | --- |  usd          | null     | --- | null                 | 1000.0             | null               | null             | 53008     | [Currency to be sent entered incorrectly] | 19055         | [Mass payment has not been processed completely. Please check transaction results] | absent         |



    Examples: check validation for toCurrency:
      | fromCurrency | fromAmount | --- | toCurrency | toAmount | --- | receivedFromCurrency | receivedFromAmount | receivedToCurrency | receivedToAmount | errorCode | errorMsgs                                     | rooterrorCode | rooterrorMsgs                                                                      | requestPattern |
      | usd          | 1000       | --- |            | null     | --- | usd                  | 1000.0             |                    | null             | 53007     | [Currency to be received entered incorrectly] | 19055         | [Mass payment has not been processed completely. Please check transaction results] | empty          |
      | usd          | 1000       | --- |            | null     | --- | usd                  | 1000.0             | null               | null             | 53007     | [Currency to be received entered incorrectly] | 19055         | [Mass payment has not been processed completely. Please check transaction results] | null           |
    #  | usd          | 1000       | --- |            | null     | --- | usd                  | 1000.0             | null               | null             | 53007     | [Currency to be received entered incorrectly] | 19055         | [Mass payment has not been processed completely. Please check transaction results] | absent         |

      #|              | 1000       | --- |            |          | --- | null                 | 1000.0             | null               | null | 53008     | [Currency to be sent entered incorrectly] | 19055         | [Mass payment has not been processed completely. Please check transaction results] | null           |
     # |              | 1000       | --- |            |          | --- | null                 | 1000.0             | null               | null | 53008     | [Currency to be sent entered incorrectly] | 19055         | [Mass payment has not been processed completely. Please check transaction results] | absent         |

  #  Examples: check validation for From/To (currency is null):
   #   | fromCurrency | fromAmount | --- | toProvider | toCurrency | toAmount | --- | receivedFromAmount | receivedFromCurrency | receivedToCurrency | errorCode | errorMsgs                                 | rooterrorCode | rooterrorMsgs                                                                      | requestPattern |
    #  |              | 1000       | --- | card       |            |          | --- | 1000.0             | null                 | null               | 53008     | [Currency to be sent entered incorrectly] | 19055         | [Mass payment has not been processed completely. Please check transaction results] | null           |
