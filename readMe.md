# BANKING SYSTEM :bank:

- You can create account. ( accountType must be : TRY , USD , EUR)
- You can get Account details by accountNumber. 
- You can deposit money with your accountNumber. ( :star2: The "last modified" header will be added to the headers)
- You can transfer money between accounts. ( :star2: Currency will be changed automatically - :star2: The "last modified" header will be added to the headers )
- You can get account logs with account number. ( :star2: CrossOrigin)
- And you can get all account details.

# 
:exclamation: You have to write your Collection API Key in exchangeServis.properties.

:exclamation: Apache kafka was used for account logs.

:star: Firstall you have to start zookeeper server and apache kafka server from console then the topic will be created automatically when the application is started.

- ### | (Post Method) Create Account |  .../api/account  
    ```
    {
  "name": "string",
  "surname": "string",
  "email": "string",
  "idendityNumber": "string",
  "type": "string"
    }
    ```

- ###  | (Put Method) Deposit |  .../api/account/{accountNumber} 
    ```
    {
  "amount": 0
    }
    ```

- ###  | (Put Method) Money Transfer Between Accounts |  .../api/account/transfer/{transferToFromAccountNumber}
    ```
    {
  "transferredAccountNumber": "string",
  "amount": 0
    }
    ```
- ###  | (Get Method) Get Account Logs Details | .../api/account/logs/{accountNumber}
- ###  | (Get Method) Get Account Details | .../api/account/{accountNumber}
- ###  | (Get Method) Get All Account Details | .../api/accounts 

#

#### CollectAPI is used in this project -> https://collectapi.com/api/economy/gold-currency-and-exchange-api/exchange

#

<img src="https://raw.githubusercontent.com/github/explore/5b3600551e122a3277c2c5368af2ad5725ffa9a1/topics/java/java.png" align="left" height="30" width="30" />
<img src= "https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/spring-boot/spring-boot.png" align="left" height="30" width="30">
<img src="https://cdn.jsdelivr.net/npm/simple-icons@v7/icons/apachekafka.svg" height="30" width="30" />
