start %1_receive.exe
timeout 1
:WaitForWindow

tasklist  /fi "WINDOWTITLE eq Direct3D11 renderer" | FINDSTR /I "%1_receive.exe"
IF ERRORLEVEL 1 (GOTO :WaitForWindow) ELSE (ECHO started)
nircmd win activate title "Direct3D11 renderer"
nircmd sendkeypress lwin+home
nircmd sendkeypress alt+enter
exit