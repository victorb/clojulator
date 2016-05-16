#! /bin/sh

set -e

echo "Cleaning..."
rm -rf www/ platforms/ plugins
echo "Copying resources/public to www/"
cp -r resources/public www/
echo "Done! Running phonegap with ios"
phonegap run ios --verbose
echo "DONE!"
