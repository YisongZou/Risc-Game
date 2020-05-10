#!/bin/bash

./gradlew build || exit 1
./gradlew cloverGenerateReport || exit 1
./scripts/coverage_summary.sh
ls -l /
ls -l /coverage-out/ || exit 1
cp -r shared/build/reports/clover/html/* /coverage-out/ || exit 1
cp -r client/build/reports/clover/html/* /coverage-out/ || exit 1
cp -r server/build/reports/clover/html/* /coverage-out/ || exit 1
