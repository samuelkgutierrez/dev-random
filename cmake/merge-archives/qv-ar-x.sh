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
# set -o xtrace

# Path to ar
ar_cmd="${1}"
# Name of the eventual merged archive.
target_name="${2}"
# Path to library that we will extract objects from.
lib_path="${3}"

lib_name=$(\basename "${lib_path}")
tmp_dir_name="${target_name}-${lib_name%.*}.objs"

# Create directory, if it doesn't already exist.
[[ ! -d "${tmp_dir_name}" ]] && \mkdir "${tmp_dir_name}"

# Extract each archive to its own subdirectory to avoid object filename clashes.
\pushd "${tmp_dir_name}" > /dev/null

${ar_cmd} -x "${lib_path}"

\popd > /dev/null

exit 0

# vim: ts=4 sts=4 sw=4 expandtab
