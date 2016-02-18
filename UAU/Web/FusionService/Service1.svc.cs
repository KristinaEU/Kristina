using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using System.Windows.Forms;

namespace FusionService
{

    public class Service1 : IService1
    {

        public string executeCommand(CommandData test)
        {
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Origin", "*");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Methods", "POST");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Headers", "Content-Type, Accept");
            try
            {
                string cmd = test.Command.ToLower();
                if (test.Target.ToLower().Equals("ssi"))
                {
                    bool  failed = false;
                    string action = UDPListener.Instance.isRunning == StatusCode.UP ? "stop" : "start";

                    if (cmd.Equals("start/stop"))
                    {
                        failed = !startStopPipe();
                    }

                    if (!failed)
                        return action + "-signal send";
                    else
                        return "failed to send " + action + "-signal";
                    
                }
                else if (test.Target.ToLower().Equals("vsm"))
                {
                    return VSMHandler.executeCommand(cmd, "res/prj/vsm");
                }
                else
                    return "Unknown target: " + test.Target + ". Valid values are: ssi, vsm";
            }
            catch (Exception ex)
            {
                return ex.Message;
            }
        }

        public Tuple<SSIEvent[], string> getEmotion()
        {
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Origin", "*");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Methods", "GET");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Headers", "Content-Type, Accept");
            return new Tuple<SSIEvent[], string>(UDPListener.Instance.CurrentValues, UDPListener.Instance.CurrentError);
        }

        public Status getStatus()
        {
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Origin", "*");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Methods", "GET");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Headers", "Content-Type, Accept");

            StatusCode pipeIsRunning = StatusCode.UNKNOWN;
            StatusCode pipeIsReceiving = StatusCode.UNKNOWN;
            StatusCode vsmIsRunning = StatusCode.UNKNOWN;

            pipeIsRunning = UDPListener.Instance.isRunning;

            //Checking if SSI-Pipeline is Receiving   
            pipeIsReceiving = UDPListener.Instance.isreceiving;

            //Checking if SVM is up
            vsmIsRunning = VSMHandler.getStatus();

            return new Status(pipeIsRunning, pipeIsReceiving, vsmIsRunning);

        }

        public List<String> getEventQueque()
        {
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Origin", "*");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Methods", "GET");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Headers", "Content-Type, Accept");
                   
            return UDPListener.Instance.EventQueque;

        }

        private bool startStopPipe()
        {

            UdpClient udpClient = new UdpClient();
            IPEndPoint ep = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 1336);
            udpClient.Connect(ep);
            Byte[] sendBytes = Encoding.ASCII.GetBytes("exit");
            try
            {
                udpClient.Send(sendBytes, sendBytes.Length);
            }
            catch (Exception e)
            {
                throw e;
                return false;
            }
            return true;
        }
    }
}