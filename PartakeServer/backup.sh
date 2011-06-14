#!/bin/sh

if ! [ -e ~/.pgpass ]; then
  /bin/echo '~/.pgpass does not exist.'
fi

/bin/cp ~/ROOT.war ~/backup/wars/ROOT-`/bin/date +%s`.war
/usr/bin/pg_dump partake -U partake > ~/backup/sqls/partake-`/bin/date +%s`.sql

