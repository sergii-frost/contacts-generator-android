# contacts-generator-android
Util app which will help generating contacts for Address Book in order to get valid international numbers from different countries among contacts

### Core package

If you want to use non-UI code in your project, feel free to grab only `core` package contents.  
It contains:  
- `ContactModel` which is simplified model for contacts from Address Book
- `RegionDataModel` which represents list of contacts per country/region
- `PhoneNumberFaker` which uses `.json` files as input for possible phone numbers
- `ContactsHelper` which is responsible for managing contacts in Address Book.

You will also need to add `.json` files for each region you want to support.  
By default `.json` files should be placed in `assets/regions` path.  
File name should be named after valid region (2 letters country code, e.g. `se` for Sweden)  

Format of the file can be like:  

```
{
  "phones": [
    "+46 70 #######",
    "+46 72 #######",
    "+46 73 #######",
    "+46 76 #######"
  ]
}
```

As a result, `PhoneNumberFaker` will generate fake possible numbers by replacing `#` with random digit from 0..9
Phone number validation is done based on `PhoneNumberUtil` which is part of Android SDK.