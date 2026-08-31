param(
    [Parameter(Mandatory = $true)][string]$CellSheet,
    [Parameter(Mandatory = $true)][string]$ComponentSheet,
    [Parameter(Mandatory = $true)][string]$AssetRoot,
    [Parameter(Mandatory = $true)][string]$ConceptRoot
)

Add-Type -AssemblyName System.Drawing

function Test-BackgroundPixel([System.Drawing.Color]$color) {
    $maximum = [Math]::Max($color.R, [Math]::Max($color.G, $color.B))
    $minimum = [Math]::Min($color.R, [Math]::Min($color.G, $color.B))
    return $color.R -ge 225 -and $color.G -ge 225 -and $color.B -ge 225 -and ($maximum - $minimum) -le 14
}

function Find-IconBounds([System.Drawing.Bitmap]$bitmap, [int]$startX, [int]$endX) {
    $left = $endX
    $right = $startX
    $top = $bitmap.Height
    $bottom = 0
    for ($y = 0; $y -lt $bitmap.Height; $y++) {
        for ($x = $startX; $x -lt $endX; $x++) {
            if (-not (Test-BackgroundPixel $bitmap.GetPixel($x, $y))) {
                $left = [Math]::Min($left, $x)
                $right = [Math]::Max($right, $x)
                $top = [Math]::Min($top, $y)
                $bottom = [Math]::Max($bottom, $y)
            }
        }
    }
    if ($right -lt $left -or $bottom -lt $top) {
        throw "No icon pixels found in sheet half $startX..$endX"
    }
    return [System.Drawing.Rectangle]::new($left, $top, $right - $left + 1, $bottom - $top + 1)
}

function Export-Icon(
    [string]$sheetPath,
    [bool]$rightHalf,
    [string]$outputPath
) {
    $source = [System.Drawing.Bitmap]::new($sheetPath)
    try {
        $half = [int]($source.Width / 2)
        $startX = if ($rightHalf) { $half } else { 0 }
        $endX = if ($rightHalf) { $source.Width } else { $half }
        $bounds = Find-IconBounds $source $startX $endX

        $cutout = [System.Drawing.Bitmap]::new($bounds.Width, $bounds.Height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        try {
            for ($y = 0; $y -lt $bounds.Height; $y++) {
                for ($x = 0; $x -lt $bounds.Width; $x++) {
                    $color = $source.GetPixel($bounds.X + $x, $bounds.Y + $y)
                    if (Test-BackgroundPixel $color) {
                        $cutout.SetPixel($x, $y, [System.Drawing.Color]::Transparent)
                    } else {
                        $cutout.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $color.R, $color.G, $color.B))
                    }
                }
            }

            $target = [System.Drawing.Bitmap]::new(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
            try {
                $maxSize = 15.0
                $scale = [Math]::Min($maxSize / $bounds.Width, $maxSize / $bounds.Height)
                $width = [Math]::Max(1, [int][Math]::Round($bounds.Width * $scale))
                $height = [Math]::Max(1, [int][Math]::Round($bounds.Height * $scale))
                $offsetX = [int][Math]::Floor((16 - $width) / 2.0)
                $offsetY = [int][Math]::Floor((16 - $height) / 2.0)

                $graphics = [System.Drawing.Graphics]::FromImage($target)
                try {
                    $graphics.Clear([System.Drawing.Color]::Transparent)
                    $graphics.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
                    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
                    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
                    $graphics.DrawImage($cutout, [System.Drawing.Rectangle]::new($offsetX, $offsetY, $width, $height))
                } finally {
                    $graphics.Dispose()
                }

                $parent = Split-Path -Parent $outputPath
                New-Item -ItemType Directory -Force -Path $parent | Out-Null
                $target.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
            } finally {
                $target.Dispose()
            }
        } finally {
            $cutout.Dispose()
        }
    } finally {
        $source.Dispose()
    }
}

New-Item -ItemType Directory -Force -Path $ConceptRoot | Out-Null
Copy-Item -LiteralPath $CellSheet -Destination (Join-Path $ConceptRoot 'essentia_cells_256k_1024k_concept.png') -Force
Copy-Item -LiteralPath $ComponentSheet -Destination (Join-Path $ConceptRoot 'essentia_components_256k_1024k_concept.png') -Force

Export-Icon $CellSheet $false (Join-Path $AssetRoot 'essentia_cell_256k.png')
Export-Icon $CellSheet $true (Join-Path $AssetRoot 'essentia_cell_1024k.png')
Export-Icon $ComponentSheet $false (Join-Path $AssetRoot 'essentia_component_256k.png')
Export-Icon $ComponentSheet $true (Join-Path $AssetRoot 'essentia_component_1024k.png')
