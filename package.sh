#!/bin/bash

ARGS=$(getopt --options p::b:c:r: --long media::,branch:,commit: --name "$0" -- "$@")

# If parsing of option failed, exit
if [ $? -ne 0 ];
then
    exit 1
fi

eval set -- "$ARGS";

# default values
DEB_REPO=
GIT_BRANCH=
GIT_COMMIT=
REPLACE_FILE=

while true; do
    case "$1" in
	-p|--media)
	    case "$2" in
		"")
		# Using default repository (on QA = qaservice1)
		    DEB_REPO=qaservice1.internal
		    ;;
		*)
		    DEB_REPO="$2"
		    ;;
	    esac
	    shift 2;
	    ;;
	-b|--branch)
	    GIT_BRANCH="$2"
	    shift 2;
	    ;;
	-c|--commit)
	    GIT_COMMIT="$2"
	    shift 2;
	    ;;
	--)
	    shift;
	    break;
	    ;;
	*)
	    echo "Unmanaged option $1"
	    ;;
    esac
done

BUILD_TARGET="debCreatePackage debMediaPackage"
if [ "$DEB_REPO" == "" ]
then
    BUILD_TARGET="debCreatePackage"
fi

if [ "$GIT_BRANCH" == "" -o "$GIT_COMMIT" == "" ]
then
    echo "Please specify the branch and commit hash (information required to compute the version of the package)"
    exit 3
fi

echo "Creating and pushing to $DEB_REPO the debian package for ${GIT_BRANCH}@${GIT_COMMIT}"

# Calling gradle with the right options
../gradlew clean $BUILD_TARGET -PgitBranch=${GIT_BRANCH} -PgitCommit=${GIT_COMMIT} -PforceDebianRepo=${DEB_REPO} && exit 0
