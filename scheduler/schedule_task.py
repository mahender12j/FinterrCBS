import datetime
import functools
import logging
import time

import mysql.connector
import schedule


class schedule_my_task():
    def __init__(self):
        self.db = mysql.connector.connect(
            host="68.183.191.246", user="root", passwd="mysql", database="malaysia")
        self.mycursor = self.db.cursor()
        logging.basicConfig(filename='myapp.log', level=logging.DEBUG)

    def with_logging(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            logging.info('LOG: Running job "%s"' % func.__name__)
            result = func(*args, **kwargs)
            logging.info('LOG: Job "%s" completed' % func.__name__)
            return result
        return wrapper

    @with_logging
    def update_daily_balance(self):
        sql = "INSERT INTO bacc_daily_balance (identifier, dateOfBal, balance)  (SELECT identifier, NOW(),balance FROM thoth_accounts)"
        self.mycursor.execute(sql)
        self.db.commit()
        print(self.mycursor.rowcount,
              "daily record inserted into bacc_daily_balance table.")

    @with_logging
    def update_weekly_balance(self):
        sql = "insert into bacc_weekly_balance (identifier, lowest_balance, week_number, weekly_bonas) (SELECT distinct identifier, min(balance), (SELECT FLOOR((DAYOFMONTH(CURRENT_DATE()) - 1) / 7) + 1 AS week_of_month) ,(min(balance)*0.03*1)/52.14 as weekly_bonas FROM bacc_daily_balance WHERE WEEKOFYEAR(dateOfBal)=WEEKOFYEAR(NOW())-1 and identifier not like 'MYMEM%' group by identifier);"
        self.mycursor.execute(sql)
        self.db.commit()
        print(self.mycursor.rowcount,
              "weekly record inserted into bacc_weekly_balance table.")

    # @with_logging
    # def update_monthly_balance(self):
    #     print('update monthly balance api call here')
