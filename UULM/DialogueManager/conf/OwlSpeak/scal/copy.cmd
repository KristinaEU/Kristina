@echo off
set /a var=26
:LOOP
if %var% gtr 100 goto :END
copy C:\OwlSpeak\light.owl C:\OwlSpeak\scal\light%var%.owl
set /a var+=1
goto LOOP
:END
pause
