import sys
import time

import schedule

import schedule_task
from daemon import Daemon


class MyDaemon(Daemon):
    def run(self):
        while True:
            schedule.run_pending()
            time.sleep(1)


if __name__ == "__main__":
    daemon = MyDaemon('/tmp/daemon-example.pid')
    task = schedule_task.schedule_my_task()
    # schedule.every(5).seconds.do(task.update_daily_balance)
    schedule.every().day.at("23:59").do(task.update_daily_balance)
    schedule.every().sunday.at("23:59").do(task.update_weekly_balance)

    if len(sys.argv) == 2:
        if 'start' == sys.argv[1]:
            daemon.start()
        elif 'stop' == sys.argv[1]:
            daemon.stop()
        elif 'restart' == sys.argv[1]:
            daemon.restart()
        else:
            print
            "Unknown command"
            sys.exit(2)
        sys.exit(0)
    else:
        print
        "usage: %s start|stop|restart" % sys.argv[0]
        sys.exit(2)
