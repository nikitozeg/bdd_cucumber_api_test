Feature: EPA Rest API mass payments test

  Background:
    And Token service is disconnected
    And Preflight is disconnected
    And Fees is disconnected
    And Payment is disconnected
    And Confirmation is disconnected
    And GET v2/payment/id state is disconnected


  Scenario Outline: Mass payment: Purse --> Card (by fullPAN) (single Payment)
    And Token service is connected
    And Token recieved for user <login>
    And Preflight is connected
    And Fees is connected

    And Payment is connected
    And Confirmation is connected

    And Add parameters to body request for payment service via "<requestPattern>" pattern by:
      | fromProvider | fromIdentity   | fromCurrency | fromAmount | toProvider | toIdentity | toCurrency | details | externalId |
      | eWaLLet      | <fromIdentity> | rub          | 1000       | card       |            | rub        | 1       | *UUID*     |
    And Add card identity to body request for payment service via "<requestPattern>" pattern by:
      | fullPAN    | cardHolder    |
      | <fullPAN1> | <cardHolder1> |

    * Root object is:
      | state | dateStarted | dateFinished | payments | id   | self | errorCode | errorMsgs |
      | New   | null        | null         | 1        | null | null | 0         | []        |
    * Payment object is:
      | id                                   | to   | from    | externalId | details | metadata | state | dateStarted | dateFinished | quote | quoteExpireIn | rate | redirectUrl | errorCode | errorMsgs |
      | 00000000-0000-0000-0000-000000000000 | card | eWaLLet | *UUID*     | 1       | null     | New   | null        | null         | null  | null          | null | null        | 0         | []        |

    * From object is:
      | provider | identity       | amount | currency | fee      |
      | eWaLLet  | <fromIdentity> | 1000.0 | RUB      | 125.0000 |

    * To object is:
      | provider | identity                                       | amount | currency | fee  |
      | card     | {cardHolder=<cardHolder1>, fullPAN=<fullPAN1>} | 1000.0 | RUB      | null |

    And Fees Request is executed
    And Fees results checked

    And Preflight Request is executed
    And Preflight results checked

    And Payment Request is executed
    And Payment results checked:
      | message                                               | confirm                                     | returnUrl | confirmationDigits | payments | id   | self | errorCode | errorMsgs                         |
      | *Please enter *,*,* digit from your payment password* | */v2/payments/*massPaymentID*/confirmation* | null      | *[*, *, *]*        | 1        | null | null | 30050     | [Confirmation code has been sent] |

    And Confirmation Request is executed
    And GET v2/payment/id state is connected
    And Payment state is COMPLETED

    * DB record in InvoicePositions table for the record #1:
      | OperationTypeId | OperationTypeName                | Amount  | Fee    | State |
      | 112             | RefilExternalCardFromPurseRussia | 1000.00 | 125.00 | 4     |

    * DB record in TPurseTransactions table for the record #1:
      | PurseSectionId | Amount    | DestinationId | Details                                                               | Direction | RefundCount | RefundedTransactionId | PurseId | CurrencyId | UserEmail          |
      | 1130512        | 1000.0000 | 25            | Outgoing payment to sided bank card MasterCard, x9437.                | out       | 0           | null                  | 571041  | 3          | <login>            |
      | 1130512        | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | out       | 0           | null                  | 571041  | 3          | <login>            |
      | 173322         | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | out       | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173322         | 1000.0000 | 25            | Outgoing payment to sided bank card MasterCard, x9437.                | in        | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173322         | 1000.0000 | 25            | Outgoing payment to sided bank card MasterCard, x9437.                | out       | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173322         | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | in        | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173321         | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | in        | 0           | null                  | 406604  | 3          | eps@qa.swiftcom.uk |

    * DB record in LimitRecords table for the record #1:
      | CurrencyId | Direction | Destination | Product        | Amount  | ProductType |
      | 3          | 2         | 25          | <fromIdentity> | 1000.00 | 1           |

    * DB record in TExternalTransactions table for the record #1:
      | Amount    | CurrencyId | ExternalServiceId | WireServiceName | Details                                                                                   |
      | 1000.0000 | 3          | 13                | BankVoronezh    | Outgoing payment to external card MasterCard, x9437 from e-Wallet 000-571041. Details: 1. |

    Examples:
      | login                      | fromIdentity | fullPAN1         | fullPAN2         | cardHolder1  | cardHolder2   | requestPattern |
      | bizSanction@qa.swiftcom.uk | 000-571041   | 5213243732809437 | 5213243732809437 | James Oliver | Andrey Ivanov | absent         |


  Scenario Outline: Mass payment: Purse --> Csarsd (by fullPAN)
    And Token service is connected
    And Token recieved for user <login>
    And Preflight is connected
    And Fees is connected

    And Payment is connected
    And Confirmation is connected

    And Add parameters to body request for payment service via "<requestPattern>" pattern by:
      | fromProvider | fromIdentity   | fromCurrency | fromAmount | toProvider | toIdentity | toCurrency | details | externalId   |
      | eWaLLet      | <fromIdentity> | rub          | 1000       | card       |            | rub        | 1       | externalId 1 |
      | eWaLLet      | <fromIdentity> | rub          | 2000       | card       |            | rub        | 2       | externalId 2 |
    And Add card identity to body request for payment service via "<requestPattern>" pattern by:
      | fullPAN    | cardHolder    |
      | <fullPAN1> | <cardHolder1> |
      | <fullPAN2> | <cardHolder2> |

    * Root object is:
      | state | dateStarted | dateFinished | payments | id   | self | errorCode | errorMsgs |
      | New   | null        | null         | 1        | null | null | 0         | []        |
    * Payment object is:
      | id                                   | to   | from    | externalId | details | metadata | state | dateStarted | dateFinished | quote | quoteExpireIn | rate | redirectUrl | errorCode | errorMsgs |
      | 00000000-0000-0000-0000-000000000000 | card | eWaLLet | null       | 1       | null     | New   | null        | null         | null  | null          | null | null        | 0         | []        |
      | 00000000-0000-0000-0000-000000000000 | card | eWaLLet | null       | 2       | null     | New   | null        | null         | null  | null          | null | null        | 0         | []        |

    * From object is:
      | provider | identity       | amount | currency | fee      |
      | eWaLLet  | <fromIdentity> | 1000.0 | RUB      | 125.0000 |
      | eWaLLet  | <fromIdentity> | 2000.0 | RUB      | 125.0000 |

    * To object is:
      | provider | identity                                       | amount | currency | fee  |
      | card     | {cardHolder=<cardHolder1>, fullPAN=<fullPAN1>} | 1000.0 | RUB      | null |
      | card     | {cardHolder=<cardHolder2>, fullPAN=<fullPAN2>} | 2000.0 | RUB      | null |

    And Fees Request is executed
    And Fees results checked

    And Preflight Request is executed
    And Preflight results checked

    And Payment Request is executed
   # And Payment results checked:
   #   | message                                               | confirm                                                        | returnUrl | confirmationDigits | payments | id   | self | errorCode | errorMsgs                         |
   #   | *Please enter 4,5,6 digit from your payment password* | /v2/payments/9530ca38-53d9-4e6e-ab18-83203bc80b8d/confirmation | null      | [4, 5, 6]          | 1        | null | null | 30050     | [Confirmation code has been sent] |
   # And Confirmation code recieved
    And Confirmation Request is executed
    And GET v2/payment/id state is connected
    And Payment state is COMPLETED

    * DB record in InvoicePositions table for the record #1:
      | OperationTypeId | OperationTypeName                | Amount  | Fee    | State |
      | 112             | RefilExternalCardFromPurseRussia | 1000.00 | 125.00 | 4     |
    * DB record in InvoicePositions table for the record #2:
      | OperationTypeId | OperationTypeName                | Amount  | Fee    | State |
      | 112             | RefilExternalCardFromPurseRussia | 2000.00 | 125.00 | 4     |

    * DB record in TPurseTransactions table for the record #1:
      | PurseSectionId | Amount    | DestinationId | Details                                                               | Direction | RefundCount | RefundedTransactionId | PurseId | CurrencyId | UserEmail          |
      | 1130512        | 1000.0000 | 25            | Outgoing payment to sided bank card MasterCard, x9437.                | out       | 0           | null                  | 571041  | 3          | <login>            |
      | 1130512        | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | out       | 0           | null                  | 571041  | 3          | <login>            |
      | 173322         | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | out       | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173322         | 1000.0000 | 25            | Outgoing payment to sided bank card MasterCard, x9437.                | in        | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173322         | 1000.0000 | 25            | Outgoing payment to sided bank card MasterCard, x9437.                | out       | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173322         | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | in        | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173321         | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | in        | 0           | null                  | 406604  | 3          | eps@qa.swiftcom.uk |
    * DB record in TPurseTransactions table for the record #2:
      | PurseSectionId | Amount    | DestinationId | Details                                                               | Direction | RefundCount | RefundedTransactionId | PurseId | CurrencyId | UserEmail          |
      | 1130512        | 2000.0000 | 25            | Outgoing payment to sided bank card MasterCard, x9437.                | out       | 0           | null                  | 571041  | 3          | <login>            |
      | 1130512        | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | out       | 0           | null                  | 571041  | 3          | <login>            |
      | 173322         | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | out       | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173322         | 2000.0000 | 25            | Outgoing payment to sided bank card MasterCard, x9437.                | in        | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173322         | 2000.0000 | 25            | Outgoing payment to sided bank card MasterCard, x9437.                | out       | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173322         | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | in        | 0           | null                  | 144177  | 3          | eps@qa.swiftcom.uk |
      | 173321         | 125.0000  | 725           | Commission for outgoing payment to sided bank card MasterCard, x9437. | in        | 0           | null                  | 406604  | 3          | eps@qa.swiftcom.uk |

    * DB record in LimitRecords table for the record #1:
      | CurrencyId | Direction | Destination | Product        | Amount  | ProductType |
      | 3          | 2         | 25          | <fromIdentity> | 1000.00 | 1           |
    * DB record in LimitRecords table for the record #2:
      | CurrencyId | Direction | Destination | Product        | Amount  | ProductType |
      | 3          | 2         | 25          | <fromIdentity> | 2000.00 | 1           |

    * DB record in TExternalTransactions table for the record #1:
      | Amount    | CurrencyId | ExternalServiceId | WireServiceName | Details                                                                                   |
      | 1000.0000 | 3          | 13                | BankVoronezh    | Outgoing payment to external card MasterCard, x9437 from e-Wallet 000-571041. Details: 1. |
    * DB record in TExternalTransactions table for the record #2:
      | Amount    | CurrencyId | ExternalServiceId | WireServiceName | Details                                                                                   |
      | 2000.0000 | 3          | 13                | BankVoronezh    | Outgoing payment to external card MasterCard, x9437 from e-Wallet 000-571041. Details: 2. |

    Examples:
      | login                      | fromIdentity | fullPAN1         | fullPAN2         | cardHolder1  | cardHolder2   | requestPattern |
      | bizSanction@qa.swiftcom.uk | 000-571041   | 5213243732809437 | 5213243732809437 | James Oliver | Andrey Ivanov | absent         |



