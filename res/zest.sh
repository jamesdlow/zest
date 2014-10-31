#!/bin/sh
#cat test.txt | tr '…' '...'
#sed -i '' -e 's/…/\.\.\./g' test.txt
#
#sed -i '' -e 's/$/\r/g' test.txt
#'s/$/\r/'
#awk 'NR > 1 { print h } { h = $0 } END { ORS = ""; print h }'
#awk '{q=p;p=$0}NR>1{print q}END{ORS = ""; print p}'

while [ $# -gt 0 ]
do
	#echo "$1"
	#sed -i '' -e 's/…/\.\.\./g' -e 's/“/"/g' -e 's/”/"/g' -e "s/’/'/g" -e "s/‘/'/g" -e "s/–/-/g" "$1"
	cat "$1" | sed -e 's/…/\.\.\./g' -e 's/[“”]/"/g' -e "s/[’‘]/'/g" -e "s/–/-/g" | awk '{q=p;p=$0}NR>1{print q}END{ORS = ""; print p}' > zest.bak
	mv zest.bak "$1"
	shift
done