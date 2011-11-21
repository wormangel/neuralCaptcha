puts "Processando imagem..."
system "convert ./res/current/captcha.gif -crop 140x45+34+0 +repage ./res/current/captcha-cropped.gif"
system "convert ./res/current/captcha-cropped.gif ./res/current/final-captcha.bmp"
system "rm ./res/current/captcha-cropped.gif"
system "convert -threshold 80% ./res/current/final-captcha.bmp ./res/current/final-captcha.bmp"
system "convert -colors 2 ./res/current/final-captcha.bmp ./res/current/final-captcha.bmp"
for i in 0..4 do
  puts "Recordando letras... #{i}"
  system "convert ./res/current/final-captcha.bmp -crop 28x45+#{i*28} +repage ./res/current/letters/#{i}.bmp"
  system "convert -scale 10 ./res/current/letters/#{i}.bmp ./res/current/letters/#{i}.bmp"
  system "convert -threshold 40% ./res/current/letters/#{i}.bmp ./res/current/letters/#{i}.bmp"
  system "convert -colors 2 ./res/current/letters/#{i}.bmp ./res/current/letters/#{i}.bmp"
end
