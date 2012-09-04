#!/bin/bash

example=$1
if [[ -z $example ]]; then
  echo "usage: highlight.sh example" 1>&2
  exit 1
fi
html=resources/public/$example.html
template=dev/templates/$example

cp src/c2_cljs_examples/$example.cljs /tmp/$example.clj
pyg_html=`pygmentize -f html /tmp/$example.clj`
cat $template-head >  $html
echo "$pyg_html"   >> $html
cat $template-tail >> $html
