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

# TODO(skg) Cleanup generated files.

include(CMakeParseArguments)

function(QV_MERGE_ARCHIVES)
    set(options)
    set(args TARGET)
    set(list_args MERGE_TARGETS)
    cmake_parse_arguments(
        PARSE_ARGV 0
        QV
        "${options}"
        "${args}"
        "${list_args}"

    )
    # Let the caller know if they called us in an unexpected way.
    foreach(arg IN LISTS QV_UNPARSED_ARGUMENTS)
        message(WARNING "Unparsed argument ${arg}")
    endforeach()

    add_library(${QV_TARGET} STATIC IMPORTED GLOBAL)

    set(TMP_DIR ${CMAKE_CURRENT_BINARY_DIR}/${QV_TARGET}-objects)
    file(MAKE_DIRECTORY ${TMP_DIR})

    set(LIB_NAME "lib${QV_TARGET}.a")
    set(LIB_PATH ${CMAKE_CURRENT_BINARY_DIR}/${LIB_NAME})

    set(MERGE_DEPS "")
    foreach(MT ${QV_MERGE_TARGETS})
        add_custom_target(
            qvi-ar-x-${QV_TARGET}-${MT}
            COMMENT "Extracting objects from ${MT} for ${LIB_NAME}"
            COMMAND ${CMAKE_SOURCE_DIR}/cmake/qv-ar-x.sh
                    ${CMAKE_AR} ${QV_TARGET} $<TARGET_FILE:${MT}>
            WORKING_DIRECTORY ${TMP_DIR}
            DEPENDS ${MT}
        )
        list(APPEND MERGE_DEPS qvi-ar-x-${QV_TARGET}-${MT})
    endforeach()

    add_custom_target(
        qvi-ar-merge-${QV_TARGET}
        COMMENT "Creating archive ${LIB_NAME}"
        COMMAND ${CMAKE_SOURCE_DIR}/cmake/qv-ar-merge.sh
                ${CMAKE_AR} ${CMAKE_RANLIB} ${QV_TARGET} ${LIB_PATH}
        WORKING_DIRECTORY ${TMP_DIR}
        DEPENDS ${MERGE_DEPS}
    )
    add_dependencies(
        ${QV_TARGET}
        qvi-ar-merge-${QV_TARGET}
    )
    set_target_properties(
        ${QV_TARGET}
        PROPERTIES
          IMPORTED_LOCATION ${LIB_PATH}
    )
endfunction(QV_MERGE_ARCHIVES)

# vim: ts=4 sts=4 sw=4 expandtab
