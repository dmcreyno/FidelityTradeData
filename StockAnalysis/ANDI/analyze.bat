set JAVA_HOME=C:\apps\jdk-11.0.3
set INPUT_HOME=-Dcom.ga.fidelity.trades.home=.\
set FIDELITY_TRADES_HOME=Z:\fidelity_trades
set LOG4J_CONF=-Dlog4j.configuration=file:%FIDELITY_TRADES_HOME%\ANDI\log4j2.xml
%JAVA_HOME%\bin\java %LOG4J_CONF% %INPUT_HOME% -classpath c:\apps\fidelityTradeAnalysis\fidelity-trades.jar com.gravanalitical.fidelity.trades.Main