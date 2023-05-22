#!/usr/bin/env python3

'''
'''
from itertools import cycle
import calendar
from datetime import date
from datetime import timedelta


def write_schedule(file) -> None:
    '''
    '''

    employe_names = [
        'Jane Foo',
        'John Bar',
        'Jake Baz'
    ]

    start_date = date.today()
    end_date = start_date + timedelta(weeks=54)

    employee_pool = cycle(employe_names)

    cal = calendar.Calendar(calendar.MONDAY)

    cur_date = start_date
    while cur_date < end_date:
        if cur_date.weekday() == cal.firstweekday:
            employee = next(employee_pool)
            file.write(f"\n{cur_date.strftime('%B %d %Y')}, ")
            file.write(cur_date.strftime('Week %W\n'))
            file.write(f'{employee}, \n')
        cur_date = cur_date + timedelta(days=1)


if __name__ == '__main__':
    with open('test.csv', 'w') as file:
        file.write(
            '## Regular Schedule\n'
            '## Monday-Thursday 5:00 p.m. to 7:30 a.m.\n'
            '## Friday-Monday   5:00 p.m. to 7:30 a.m.\n'
        )
        write_schedule(file)

# vim: ft=python ts=4 sts=4 sw=4 expandtab
