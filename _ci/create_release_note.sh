#!/usr/bin/env bash

set -euo pipefail

origin=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd) || exit

# git fetch --tags

playstore_tags=$(git tag -l --format "%(refname:short)" --sort=-version:refname --merged HEAD "v*.*.*.*-playstore")
if [ $# -eq 0 ] && command -v fzf &> /dev/null; then
  previous_tag=$(printf "%s\n" "${playstore_tags}" \
            | fzf --prompt "Choose the previous release tag to use" \
                  --height ~7 \
                  --layout=reverse-list \
                  || true)
  release_tag=$(printf "%s\n" "${playstore_tags}" \
            | fzf --prompt "Choose the current release tag to use" \
                  --height ~7 \
                  --layout=reverse-list \
                  || true)
else
  previous_tag="${1:-$(echo "${playstore_tags}" | head -n 1)}"
  release_tag="${2:-$(echo "${playstore_tags}" | head -n 2 |  tail -n 1)}"
fi

previous_tag_sha1="$(git rev-parse "${previous_tag}^0")"
release_tag_sha1="$(git rev-parse "${release_tag}^0")"

[ "$(git tag -l "${previous_tag}")" ] || (echo "Previous release tag '${previous_tag}' not found" && exit 1)
[ "$(git tag -l "${release_tag}")" ] || (echo "Current release tag '${release_tag}' not found"; exit 1)
[ "${previous_tag}" = "${release_tag}" ] && (echo "Previous & current release tags are the same '${release_tag}'"; exit 1)

if ! git rev-list "${previous_tag}"^.."${release_tag}" | grep -q "${previous_tag_sha1}"; then
  echo "Can't find previous release tag '${previous_tag}' (${previous_tag_sha1}) before current release tag '${release_tag}' (${release_tag_sha1})"
fi

git log --first-parent --pretty='%H%x09%s' --topo-order  "${previous_tag}".."${release_tag}"

changelog=$(git show "${release_tag}":fastlane/metadata/store/en-US/changelogs/default.txt)
echo "CHANGELOG: ${changelog}"


# if [[ "${previous_tag}" =~ ^v([0-9]+)\.([0-9]+)\.([0-9]+)\.([0-9]+)-playstore$ ]]; then
#     major="${BASH_REMATCH[1]}"
#     minor="${BASH_REMATCH[2]}"
#     patch="${BASH_REMATCH[3]}"
#     build="${BASH_REMATCH[4]}"
    
#     # Print the extracted values
#     echo "major: $major"
#     echo "minor: $minor"
#     echo "patch: $patch"
#     echo "build: $build"
# else
#     echo "Tag format should follow 'vX.Y.Z.B-playstore' format"
# fi
