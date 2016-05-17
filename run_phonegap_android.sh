#! /bin/sh

set -e

echo "Building"
lein do clean, cljsbuild once min
echo "Cleaning..."
rm -rf www/ platforms/ plugins
echo "Copying resources/public to www/"
cp -r resources/public www/
echo "Done! Running phonegap with ios"
phonegap run android --device --verbose
echo "DONE!"
