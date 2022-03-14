#!/bin/bash
#
# Copyright (c)      2022 Samuel K. Gutierrez
#                         All rights reserved.
#
# BSD 3-Clause License
# 
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# 
# * Redistributions of source code must retain the above copyright notice, this
#   list of conditions and the following disclaimer.
# 
# * Redistributions in binary form must reproduce the above copyright notice,
#   this list of conditions and the following disclaimer in the documentation
#   and/or other materials provided with the distribution.
# 
# * Neither the name of the copyright holder nor the names of its
#   contributors may be used to endorse or promote products derived from
#   this software without specific prior written permission.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
# SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# Exit when any command fails.
set -e
#set -o xtrace

# Path to ar
ar_cmd="${1}"
# Path to ranlib
ranlib_cmd="${2}"
# Target name of the merged archive.
target_name="${3}"
# Path to where we place the newly created archive.
archive_dest="${4}"
# Name of the merged archive.
archive_name="lib${target_name}.a"

all_objs=""
for d in "${target_name}"-*.objs; do
    all_objs+="$(\printf "%s " "$(\find "${d}" -name \*.o)")"
done

# Generate archive.
\echo "${all_objs}" | \xargs "${ar_cmd}" -r "${archive_name}"
${ranlib_cmd} "${archive_name}"

\mv -f "${archive_name}" "${archive_dest}"

exit 0

# vim: ts=4 sts=4 sw=4 expandtab
