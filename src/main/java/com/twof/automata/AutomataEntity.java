package com.twof.automata;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

class Server  {
    private static final int PORT = 9123;
    private AutomataEntity automata;

    public Server(AutomataEntity automata) {
        this.automata = automata;
    }

    public void createServerSocketChannel() throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));

        acceptor.setHandler( new  TimeServerHandler(this.automata) );
        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
        acceptor.bind( new InetSocketAddress(PORT) );
    }
}

public class AutomataEntity extends PathAwareEntity {
    String filename = System.getProperty("user.home") + "/tmp/enchantment.txt";

    protected AutomataEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        // open up stream and begin reading and writing
        // automata will respond to the stream when they're ready for the next instruction
        // this is to control execution speed

        Server server = new Server(this);
        try {
            server.createServerSocketChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Got here");
    }

    public void performAction(String action) {
        switch (action) {
            case "jump": jump(); break;
            default: break;
        }
    }
}

class TimeServerHandler extends IoHandlerAdapter
{
    private AutomataEntity automata;

    TimeServerHandler(AutomataEntity automata) {
        this.automata = automata;
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause ) throws Exception
    {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
        String str = message.toString();
        if( str.trim().equalsIgnoreCase("quit") ) {
            session.closeNow();
            return;
        }

        automata.performAction(str);

        System.out.println("Message written...");
        System.out.println(str);
    }

    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
    {
        System.out.println( "IDLE " + session.getIdleCount( status ));
    }
}
