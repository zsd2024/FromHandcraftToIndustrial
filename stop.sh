#!/bin/bash

git config --unset http.proxy
git config --unset https.proxy
kill $(ps aux | grep clash | grep -v grep | awk '{print $2}')
