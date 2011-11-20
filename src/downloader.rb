require 'open-uri'
require 'net/http'

Net::HTTP.start('www.snaphost.com') do |http|
  puts "Downloading image..."
  resp = http.get( '/captcha/CaptchaImage.aspx?id=DEMO12345678' )
  open("./res/current/captcha.gif", 'w') do |file|
    file.write(resp.body)
  end
  puts "Downloaded!"
end
