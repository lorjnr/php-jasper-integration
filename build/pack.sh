#!/bin/bash

HERE=`dirname "$0"`

if [ -f "$HERE/release_properties" ]; then
  . "$HERE/release_properties"
else
  echo "release_properties not found"
  exit 1
fi

DIR=`mktemp -d`
SOURCE="$DIR/source"
mkdir "$SOURCE"
TARGET="$DIR/target/$PACKAGE_NAME"
mkdir -p "$TARGET"

echo "Use directory $SOURCE"

echo "Export source from $SVN_ROOT/$SVN_DIR/src"
svn export "$SVN_ROOT/$SVN_DIR/src" "$SOURCE/src"
if [ ! -d "$SOURCE/src" ]; then
  echo "Unable to get source"
  exit 1
fi

echo "Export files from $SVN_ROOT/$SVN_DIR/var"
svn export "$SVN_ROOT/$SVN_DIR/var" "$SOURCE/var"
if [ ! -d "$SOURCE/var" ]; then
  echo "Unable to get files"
  exit 1
fi

mv "$SOURCE/var/iReport-3.0.0" "$TARGET"
mv "$SOURCE/var/jaspit" "$TARGET"
mv "$SOURCE/var/jaspServer" "$TARGET"
mv "$SOURCE/src/jaspext" "$TARGET"
mv "$SOURCE/src/jaspinteg" "$TARGET"

pushd "$SOURCE/src/java"
ant -f jasperintegration.xml
if [ $? -ne 0 ]; then
  popd
  echo "Unable to generate jasp.jar"
  exit 1
fi
popd

cp "$SOURCE/src/java/out/artifacts/jasp/jasp.jar" "$TARGET/jaspit"
cp "$SOURCE/src/java/out/artifacts/jasp/jasp.jar" "$TARGET/jaspServer/lib"

cp "$SOURCE/src/java/lib/commons-beanutils-1.7.jar" "$TARGET/jaspServer/lib"
cp "$SOURCE/src/java/lib/commons-collections-2.1.jar" "$TARGET/jaspServer/lib"
cp "$SOURCE/src/java/lib/commons-digester-1.7.jar" "$TARGET/jaspServer/lib"
cp "$SOURCE/src/java/lib/commons-logging-1.0.2.jar" "$TARGET/jaspServer/lib"
cp "$SOURCE/src/java/lib/itext-1.3.1.jar" "$TARGET/jaspServer/lib"
cp "$SOURCE/src/java/lib/jasperreports-3.0.1.jar" "$TARGET/jaspServer/lib"
cp "$SOURCE/src/java/lib/mysql-connector-java-3.1.11-bin.jar" "$TARGET/jaspServer/lib"

cp "$SOURCE/src/java/lib/commons-beanutils-1.7.jar" "$TARGET/jaspit/lib"
cp "$SOURCE/src/java/lib/commons-collections-2.1.jar" "$TARGET/jaspit/lib"
cp "$SOURCE/src/java/lib/commons-digester-1.7.jar" "$TARGET/jaspit/lib"
cp "$SOURCE/src/java/lib/commons-logging-1.0.2.jar" "$TARGET/jaspit/lib"
cp "$SOURCE/src/java/lib/jdom.jar" "$TARGET/jaspit/lib"
cp "$SOURCE/src/java/lib/jasperreports-3.0.1.jar" "$TARGET/jaspit/lib"

cp "$SOURCE/var/README" "$TARGET"

pushd "$DIR/target"
tar cpvBf "$PACKAGE_FILE_NAME" .
popd

mv "$DIR/target/$PACKAGE_FILE_NAME" .

rm -rf "$DIR"

exit 0


