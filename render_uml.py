import zlib
import requests
import base64

def plantuml_encode(puml):
    """Encodes PlantUML text into a format suitable for the PlantUML server URL."""
    # 1. UTF-8 encode
    puml_utf8 = puml.encode('utf-8')
    # 2. Compress using zlib (Deflate)
    # PlantUML server expects no zlib headers, so we use -15 for wbits
    compressor = zlib.compressobj(9, zlib.DEFLATED, -15)
    compressed = compressor.compress(puml_utf8) + compressor.flush()
    
    # 3. Custom Base64-like encoding
    return encode_64(compressed)

def encode_64(data):
    """PlantUML's custom base64 encoding."""
    res = ""
    for i in range(0, len(data), 3):
        if i + 2 == len(data):
            res += _encode_3_bytes(data[i], data[i+1], 0)
        elif i + 1 == len(data):
            res += _encode_3_bytes(data[i], 0, 0)
        else:
            res += _encode_3_bytes(data[i], data[i+1], data[i+2])
    return res

def _encode_3_bytes(b1, b2, b3):
    c1 = b1 >> 2
    c2 = ((b1 & 0x3) << 4) | (b2 >> 4)
    c3 = ((b2 & 0xF) << 2) | (b3 >> 6)
    c4 = b3 & 0x3F
    res = ""
    for c in [c1, c2, c3, c4]:
        res += _encode_6_bit(c & 0x3F)
    return res

def _encode_6_bit(b):
    if b < 10: return chr(48 + b)
    b -= 10
    if b < 26: return chr(65 + b)
    b -= 26
    if b < 26: return chr(97 + b)
    b -= 26
    if b == 0: return '-'
    if b == 1: return '_'
    return '?'

puml_content = """
@startuml
skinparam packageStyle rectangle
skinparam shadowing false
skinparam classAttributeIconSize 0

package com.springlesson.filemimechecker {
    class FileMimeCheckerApplication {
        + main(args: String[])
    }

    package core {
        class FileMimeCore {
            - strategy: MimeDetectionStrategy
            + FileMimeCore()
            + FileMimeCore(strategy: MimeDetectionStrategy)
            + process(data: byte[]): MimeDetectionResult
            + uncompressZippedByte(zipData: byte[]): List<byte[]>
        }

        package strategy {
            interface MimeDetectionStrategy {
                + detect(data: byte[]): String
            }
            class TikaMimeDetectionStrategy {
                - tika: Tika
                + detect(data: byte[]): String
            }
            MimeDetectionStrategy <|.. TikaMimeDetectionStrategy
        }

        package model {
            class MimeDetectionResult {
                - mimeTypes: List<String>
                - MimeDetectionResult(builder: Builder)
                + getMimeTypes(): List<String>
                + getFormattedResult(): String
                + {static} builder(): Builder
            }
            class Builder {
                - mimeTypes: List<String>
                + addMimeType(mimeType: String): Builder
                + addMimeTypes(mimeTypes: List<String>): Builder
                + build(): MimeDetectionResult
            }
            MimeDetectionResult +-- Builder
        }

        FileMimeCore o-- MimeDetectionStrategy
        FileMimeCore ..> MimeDetectionResult : creates
    }

    package cli {
        class CLIFileMimeController {
            - core: FileMimeCore
            + CLIDetectFiletype(filePath: String): String
        }
        CLIFileMimeController o-- FileMimeCore
    }

    package web {
        class FileMimeController {
            - fileMimeCore: FileMimeCore
            + uploadFile(file: MultipartFile): String
        }
        FileMimeController o-- FileMimeCore
    }
}
@enduml
"""

encoded = plantuml_encode(puml_content)
url = f"http://www.plantuml.com/plantuml/png/{encoded}"

print(f"Requesting UML image from: {url}")
response = requests.get(url)

if response.status_code == 200:
    with open("project_uml.png", "wb") as f:
        f.write(response.content)
    print("UML diagram has been saved to project_uml.png")
else:
    print(f"Failed to retrieve image. Status code: {response.status_code}")
