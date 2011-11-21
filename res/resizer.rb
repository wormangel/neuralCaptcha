require 'find'

i = 0
Find.find('training3/') do |f|
  if f.match(/\.bmp\Z/)
    puts i
    system "convert -scale 20 #{f} #{f}"
    system "convert -threshold 40% #{f} #{f}"
    system "convert -colors 2 #{f} #{f}"
    i += 1
  end
end
