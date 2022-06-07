start %1_receive.exe
timeout 1
:WaitForWindow

tasklist  /fi "WINDOWTITLE eq Direct3D11 renderer" | FINDSTR /I "%1_receive.exe"
IF ERRORLEVEL 1 (GOTO :WaitForWindow) ELSE (ECHO started)
nircmd win -style title "Direct3D11 renderer" 0x00C00000
powershell -File max_window.ps1 %1_receive
exit