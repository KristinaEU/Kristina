using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Timers;
using System.Web;

namespace FusionService
{
    public sealed class UDPListener
    {
        public const int MAXEVENTS = 2000;
        public StatusCode isreceiving = StatusCode.UNKNOWN;
        public StatusCode isRunning = StatusCode.UNKNOWN;

        private string currentError = "";
        private Timer timer = new Timer();
        private SSIEvent[] currentValues;
        private List<String> eventQueque = new List<String>();
        private static readonly UDPListener instance = new UDPListener();


        private UDPListener()
        {
            BackgroundWorker listener = new BackgroundWorker();
            listener.DoWork += listen;
            listener.RunWorkerAsync();
            currentValues = new SSIEvent[4] { new SSIEvent("GESTURE", 0, 0), new SSIEvent("MIMIC", 0, 0), new SSIEvent("PROSODY", 0, 0), new SSIEvent("FUSION", 0, 0) };
            timer.Interval = 2000;
            timer.AutoReset = true;
            timer.Elapsed += timerTick;
            timer.Start();
        }

        public static UDPListener Instance
        {
            get
            {
                return instance;
            }
        }

        public SSIEvent[] CurrentValues
        {
            get
            {
                return currentValues;
            }
        }

        public List<string> EventQueque
        {
            get
            {
                List<string> eq = new List<string>(eventQueque);
                eventQueque.Clear();
                return eq;
            }
        }


        public string CurrentError
        {
            get
            {
                return currentError;
            }
        }


        private void listen(object sender, DoWorkEventArgs e)
        {
            const int listenPort = 1337;
            bool done = false;
            UdpClient listener = new UdpClient(listenPort);
            IPEndPoint groupEP = new IPEndPoint(IPAddress.Any, listenPort);
            string received_data;
            byte[] receive_byte_array;
            try
            {
                while (!done)
                {
                    if (listener.Available > 0)
                    {
                        receive_byte_array = listener.Receive(ref groupEP);
                        received_data = Encoding.ASCII.GetString(receive_byte_array, 0, receive_byte_array.Length);
                        try {
                            addEventToQueque(received_data); 
                            SSIEvent tmp = XMLParser.parseEvent(received_data);
                            //Use snr to detect if we receive any audio
                            if (tmp.Origin.Equals("ACTIVITY"))
                            {
                                isreceiving = tmp.EventType.Equals("CONTINUED") ? StatusCode.UP : StatusCode.DOWN; 
                            }
                            else if (tmp.Origin.Equals("HEARTBEAT"))
                            {
                                isRunning = StatusCode.UP;
                                //resetting the timer when a heartbeat is detected
                                timer.Interval = 2000;
                            }
                            else {
                                SSIEvent em = CurrentValues.SingleOrDefault(item => item.Origin.Equals(tmp.Origin));
                                em.Arousal = tmp.Arousal;
                                em.Valence = tmp.Valence;
                            }
                        }
                        catch(Exception ex)
                        {
                            currentError = ex.Message;         
                        }
                         
                    }

                }
            }
            catch (Exception ex)
            {
                currentError = ex.Message;
            }
            finally
            {
                listener.Close();
            }
        }

       private static void timerTick(Object source, System.Timers.ElapsedEventArgs e)
        {
            UDPListener.Instance.isRunning = StatusCode.DOWN;
        }

        private void addEventToQueque(string ev) {
            if (eventQueque.Count > MAXEVENTS)
                eventQueque.Remove(eventQueque.Last());
            eventQueque.Insert(0, ev);
        }
    }
}
