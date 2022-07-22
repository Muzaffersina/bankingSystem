# BANKING SYSTEM :bank:

## You can save to file or database. (account information and logs)
:point_right: You can make this selection from the data Access  package.

- You can create account. ( accountType must be : TRY , USD , EUR)
- You can get Account details by accountNumber. 
- You can deposit money with your accountNumber. ( :star2: The "last modified" header will be added to the headers)
- You can transfer money between accounts. ( :star2: Currency will be changed automatically - :star2: The "last modified" header will be added to the headers )
- You can get account logs with account number. ( :star2: CrossOrigin)
- You can soft delete Account with accountNumber. :star2: The "last modified" header will be added to the headers)
- And you can get all account details.

# 


:exclamation: If you want to save in database, you need to make the necessary configurations from the myBatis_conf.xml.

:exclamation: You have to write your Collection API Key in exchangeServis.properties.

:exclamation: Apache kafka was used for account logs.

:star: Firstall you have to start zookeeper server and apache kafka server from console then the topic will be created automatically when the application is started.


| **Method**        | **Url**                     | **Description**        | **Body** |
| ------------- |:-------------                          | :------------      | :------------| 
| Post          | .../api/account                               | Create Account| Necessary| 
| Put           | .../api/account/{accountNumber}               | Deposit | Necessary| 
| Put           | .../api/account/transfer/{senderAccountNumber}| Money Transfer Between Accounts| Necessary| 
| Delete        | .../api/account/{accountNumber}      | Soft Delete Account |Not Necessary| 
| Get           | .../api/account/logs/{accountNumber} | Account Logs Details|Not Necessary| 
| Get           | .../api/account/{accountNumber}      | Get Account Details |Not Necessary| 
| Get           | .../api/accounts                     | Get All Accounts Details| Not Necessary| 

- Body;

  - For Create Account;
      ```
    {
    "name": "string",
    "surname": "string",
    "email": "string",
    "idendityNumber": "string",
    "type": "string"
    }
      ```

  - For Deposit; 

    ```
    {
     "amount": 0
    }
    ```

  - For Money Transfer Between Accounts;
    ```   
    {
      "transferredAccountNumber": "string",
      "amount": 0
    }
    ```

#### CollectAPI is used in this project -> https://collectapi.com/api/economy/gold-currency-and-exchange-api/exchange

#

<img src="https://raw.githubusercontent.com/github/explore/5b3600551e122a3277c2c5368af2ad5725ffa9a1/topics/java/java.png" align="left" height="30" width="30" />
<img src= "https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/spring-boot/spring-boot.png" align="left" height="30" width="30">
<img src="https://cdn.jsdelivr.net/npm/simple-icons@v7/icons/apachekafka.svg" height="30" width="30" />
