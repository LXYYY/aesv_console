$process = $args[0]
$interval = $args[1]

while (1) {
  $pid_ = 0
  $cpu = 0
  $mem = 0
  $net=0

  $Res = Get-Process -Name $process -ErrorAction SilentlyContinue

  if ($Res) {
    $running = 1

  }
  else {
    $running = 0
  }

  $pid_ = $Res.Id
  $cpu = $Res.CPU / $Res.TotalProcessorTime.Milliseconds
  $mem = $Res.WorkingSet64 / 1024 / 1024


  echo "$running $pid_ $cpu $mem $net"

  sleep($interval)
}