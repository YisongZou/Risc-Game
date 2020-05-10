#!/bin/bash

./gradlew build || exit 1
./gradlew cloverAggregateReports || exit 1
./scripts/coverage_summary.sh
ls -l /
ls -l /coverage-out/ || exit 1
cp -r server/build/reports/clover/html/* /coverage-out/ || exit 1
