#!/bin/env python3

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

    start_year = date.today().year
    start_month = date.today().month
    end_month = 12
    end_date = date.today()

    employee_pool = cycle(employe_names)

    cal = calendar.Calendar(calendar.MONDAY)

    for month in range(start_month, end_month + 1):
        month_dates = cal.itermonthdates(start_year, month)

        for md in month_dates:
            if md.weekday() == cal.firstweekday:
                employee = next(employee_pool)
                start_date = md
                file.write(f"\n{start_date.strftime('%B %d %Y')}, ")
                file.write(start_date.strftime('Week %W\n'))
                file.write(f'{employee}, \n')
                end_date = start_date + timedelta(days=7)

    for _ in range(0, 2):
        employee = next(employee_pool)
        start_date = end_date
        file.write(f"\n{start_date.strftime('%B %d %Y')}, ")
        file.write(start_date.strftime('Week %W\n'))
        file.write(f'{employee}, \n')
        end_date = start_date + timedelta(days=7)


if __name__ == '__main__':
    with open('test.csv', 'w') as file:
        file.write(
            '## Regular Schedule\n'
            '## Monday-Thursday 5:00 p.m. to 7:30 a.m.\n'
            '## Friday-Monday   5:00 p.m. to 7:30 a.m.\n'
        )
        write_schedule(file)

# vim: ft=python ts=4 sts=4 sw=4 expandtab
