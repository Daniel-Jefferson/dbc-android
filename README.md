# dbc-budsbank-android

To setup your local dev environment, you'll need to setup the following:

**Database (MySQL)**

- Install and setup MySQL Community Edition + Workbench: https://www.mysql.com/products/community
- Set the Root Account Password during setup (make sure to write this down, we'll need it later)
- After it finishes installing, launch MySQL Workbench
- Click on New Connection and set the connection name to localhost then click on Ok
- Double-click on the new connection and enter your Root Account password
- On the left sidebar, click on Schemas then click on the Create Schema icon (looks like a database symbol)
- For schema name, type in budsbank_local_dev then click on Apply
- Click on Server from the main menu then Data Import
- Select Import from Self-Contained File and select the budsbank_local_dev.sql that is attached to this card then click on Start Import
- MySQL server should always be running in the background so you don't need to do anything else to start it

**API (NodeJS)**

- Edit the /config/db.js file to reflect the attached screenshot. Replace 'password' with whatever you chose for Root Account
- To start the node server, type *node .*  in terminal then hit enter
- Confirm its running by going to http://localhost:3300 in your web browser

**Android**

- In /app/src/main/java/com/app/budbank/web/WebConfig.java, change the BASE_URL to http://10.0.2.2:3300/ (this is the equivalent of localhost or 127.0.0.1)
- Open a browser in your emulator and try to go to http://10.0.2.2:3300/, you should see a message saying backend is running
- Run the app and it should now be pointing to http://10.0.2.2:3300 (which is really localhost:3300)