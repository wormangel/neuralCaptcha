puts "Processando imagem..."
system "convert ./res/current/captcha.gif -crop 140x45+34+0 +repage ./res/current/captcha-cropped.gif"
system "convert ./res/current/captcha-cropped.gif ./res/current/captcha-cropped.bmp"
system "rm ./res/current/captcha-cropped.gif"
system "convert -threshold 80% ./res/current/captcha-cropped.bmp ./res/current/captcha-pb.bmp"
system "convert -colors 2 ./res/current/captcha-pb.bmp ./res/current/final-captcha.bmp"
system "rm ./res/current/captcha-pb.bmp"
for i in 0..4 do
  puts "Recordando letras... #{i}"
  system "convert ./res/current/final-captcha.bmp -crop 28x45+#{i*28} +repage ./res/current/letters/#{i}.bmp"
end
