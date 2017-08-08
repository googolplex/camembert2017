#!/bin/bash
git config --global user.email "googolplex@lcompras.biz"
git config --global user.name "googol plex"
rm -R *.*~
cp -R *.sh camembert2017/gitfeliz
cp -R /home/xoldfusion/workspace/camembert2017/* ./camembert2017/
cd camembert2017
git add -A
git commit -a -m "habilito el proyecto camembert"
git push -u origin master
