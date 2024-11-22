"use client"

import { ReactFlow, ReactFlowProvider, Node, Edge, Background, Controls, useReactFlow, useNodesState, useEdgesState, Connection, addEdge, MiniMap } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { Box, Button, Modal, Typography } from '@mui/material';
import { useCallback, useState, useEffect } from 'react';
import { getCookie } from 'cookies-next';
import { useRouter } from 'next/navigation';

import SelectReaction from '@/components/nodes/select-reaction';
import SelectAction from '@/components/nodes/select-action';
import { DnDProvider, useDnD } from '@/components/nodes/dnd';
import Sidebar from '@/components/nodes/sidebar';
import '@/public/css/sidebar.css'
import AddIcon from '@mui/icons-material/Add';
import HelpIcon from '@mui/icons-material/Help';

import { LogosDiscordIcon } from '@/components/icons/discordLogo';
import { LogosGoogleGmail } from '@/components/icons/gmailLogo';
import { SimpleIconsOsu } from '@/components/icons/osuLogo'

import { borderRadius, styled } from "@mui/system";
import { LogosSpotifyIcon } from '@/components/icons/spotifyLogo';
import { AkarIconsThreadsFill } from '@/components/icons/threadsLogo';
import { SimpleIconsRiotgames } from '@/components/icons/riotLogo';
import '@/public/css/tasks.css'
import { useTranslations } from 'next-intl';

const StyledBox = styled(Box)(() => ({
  width: "40%",
  backgroundColor: "#fff",
  borderRadius: "8px",
  padding: "24px",
  boxShadow: "0 4px 12px rgba(0, 0, 0, 0.2)",
}));

const initialEdges = [];

const nodeTypes = {
  'selectReaction': SelectReaction,
  'selectAction': SelectAction
}

const colorTypes = {
  'selectReaction': 'red',
  'selectAction': 'purple'
}

let id = 0;
const getId = () => `${id++}`;

function Flow() {
  const t = useTranslations('TasksPage');

  const [messages, setMessages] = useState({});

  const { screenToFlowPosition } = useReactFlow();
  const [type] = useDnD();

  const exportToJson = () => {
    const flowData = {
      nodes: nodes.map((node) => ({
        id: node.id,
        type: node.type,
        data: {
          value: {
            message: messages[node.id] || "",
          }
        },
      })),
      edges: edges.map((edge) => ({
        id: edge.id,
        source: edge.source,
        target: edge.target,
        type: edge.type,
      })),
    };
    console.log(JSON.stringify(flowData))
    console.log(JSON.stringify(buildActionPayload(flowData)));
    sendData(buildActionPayload(flowData));
  }

  const sendData = async (payload) => {
    if (payload === "") {
      return;
    }
    try {
        const response = await fetch('http://localhost:8080/actionreaction/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getCookie('userToken'),
                'UserId': String(getCookie('clientId')),
            },
            body: JSON.stringify(payload),
        });

        if (!response.ok) {
            console.error("error", response)
            return;
        }

        const data = await response.json();
        console.log("Réussi ?", data);

    } catch (error) {
        console.error(error);
        return;
    }
    location.reload();
};

function buildActionPayload(graph) {
  const { nodes, edges } = graph;

  if (nodes.length === 0 || edges.length === 0) {
    return "";
  }

  const date = new Date().toISOString().split('T')[0];
  const actionNode = nodes.find(node => node.type === "selectAction");
  let { action, field1, field2, field3, nameList: actionNameList, actionTitle } = actionNode.data.value.message;

  actionTitle = actionTitle || "New Action";

  const fields = [field1, field2, field3];

  const actionFields = {};
  actionNameList.forEach((name, index) => {
    actionFields[name] = fields[index] || "";
  });

  const actionValues = {
    Action: action,
    actionName: actionTitle,
    ...actionFields
  };

  function collectReactions(nodeId, collectedReactions = [], visited = new Set()) {
    if (visited.has(nodeId)) return;
    visited.add(nodeId);

    const linkedEdges = edges.filter(edge => edge.source === nodeId);
    linkedEdges.forEach((edge, index) => {
      const reactionNode = nodes.find(node => node.id === edge.target && node.type === "selectReaction");
      if (reactionNode) {
        const { action: reactionAction, field1, field2, field3, field4, nameList: reactionNameList } = reactionNode.data.value.message;

        const fields = [field1, field2, field3, field4]

        const reactionFields = {};
        reactionNameList.forEach((name, index) => {
            reactionFields[name] = fields[index] || "";
        });

        const valuesObject = {
          Reaction: reactionAction,
          ...reactionFields
        };

        const values = JSON.stringify(valuesObject);

        collectedReactions.push({
          id: null,
          actionTable: null,
          orderReactions: collectedReactions.length + 1,
          wait: 0,
          service: reactionNode.data.value.message.service,
          values,
          date
        });
        collectReactions(reactionNode.id, collectedReactions, visited);
      }
    });
    return collectedReactions;
  }

  const reactionsList = collectReactions(actionNode.id);

  return {
    userId: String(getCookie('clientId')),
    serviceAction: actionNode.data.value.message.service,
    actionValues: JSON.stringify(actionValues),
    date,
    reactionsList
  };
}


  const initialNodes = [
    {
      id: getId(),
      type: 'selectAction',
      position: { x: 100, y: 100 },
      data: {
        value: "",
        onChange: (id, newValue) => {
          setMessages(prev => ({ ...prev, [id]: newValue }));
        },
      },
      style: {
        background: '#fff',
        border: '3px solid purple',
        borderRadius: '20px'
      },
    }
  ];

  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);

  const handleNodesChange = useCallback((changes) => {
    const filteredChanges = changes.filter(
      (change) => !(change.type === 'remove' && change.id === '0')
    );
    onNodesChange(filteredChanges);
  }, [onNodesChange]);

  const onConnect = useCallback((connection) => {
    if (connection.source === connection.target) {
      return;
    }
    const edge = { ...connection, animated: true, id: `${edges.length + 1}`,
      style: {
        stroke: 'black',
        strokeWidth: 4
      }
    };
    setEdges((prevEdges) => addEdge(edge, prevEdges))
  }, [edges.length])

  const onDragOver = useCallback((event) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  const onDrop = useCallback(
    (event) => {
      event.preventDefault();

      if (!type) {
        return;
      }

      const position = screenToFlowPosition({
        x: event.clientX,
        y: event.clientY,
      });
      const newNode = {
        id: getId(),
        type,
        position,
        data: {
          value: "",
          onChange: (id, newValue) => {
            setMessages(prev => ({ ...prev, [id]: newValue }));
          },
        },
        style: {
          background: '#fff',
          border: `3px solid ${colorTypes[type]}`,
          borderRadius: '19px'
        },
      };

      setNodes((nds) => nds.concat(newNode));
    },
    [screenToFlowPosition, type],
  );

  return (
    <>
    <ReactFlow
      style={{borderTopLeftRadius: '15px', borderBottomLeftRadius: '15px'}}
      nodes={nodes}
      edges={edges}
      onNodesChange={handleNodesChange}
      onEdgesChange={onEdgesChange}
      onConnect={onConnect}
      nodeTypes={nodeTypes}
      onDrop={onDrop}
      onDragOver={onDragOver}
    >
      <Background bgColor='#C7C2D8'/>
      <Controls />
    </ReactFlow>
    <div className="flex xl:justify-self-start md:justify-end xs:justify-end mt-6">
        <Button
          onClick={exportToJson}
          className="bg-gradient-to-r from-purple-500 to-orange-500 text-white font-semibold rounded-lg shadow-md hover:from-purple-600 hover:to-orange-600 hover:shadow-lg transform hover:-translate-y-0.5 transition-all duration-300 px-4 py-2"
        >
          <AddIcon className="font-bold text-white" /> {t('create')}
        </Button>
    </div>
    </>
  );
}

export default function TestPage() {

  const router = useRouter();

  const [showHelp, setShowHelp] = useState(false);

  const t = useTranslations('TasksPage');

  useEffect(() => {
    const userToken = getCookie("userToken");
    const userId = getCookie("clientId");

    if (userToken === undefined && userId === undefined) {
        const currentLocale = getCookie("NEXT_LOCALE") || "en";
        router.push(`/${currentLocale}/login`);
    }
  }, [router]);

  return (
    <>
    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center' }}>
        <Typography component="h2" variant="h6" sx={{ display: 'flex', alignItems: 'center', mr: 1 }} className="text-3xl">
          {t('title')}
        </Typography>
        <LogosDiscordIcon fontSize='larger' style={{marginLeft: '0.5em'}}/>
        <LogosGoogleGmail fontSize='larger' style={{marginLeft: '1em'}} />
        <LogosSpotifyIcon fontSize='larger' style={{marginLeft: '0.75em'}} />
        <SimpleIconsOsu fontSize='larger' color='#f062a1' style={{marginLeft: '0.5em'}}/>
        <AkarIconsThreadsFill fontSize='larger' style={{marginLeft: '0.5em'}}/>
        <SimpleIconsRiotgames fontSize='larger' color='#D12935' style={{marginLeft: '0.5em'}}/>
      </Box>

  <Button
      variant="contained"
      sx={{
          backgroundColor: '#9c27b0',
          color: 'white',
          '&:hover': {
              backgroundColor: '#9028a3'
          },
          mr: 2
      }}
      onClick={() => { setShowHelp(true) }}
  >
      {t('help')}&nbsp;<HelpIcon />
  </Button>
    </Box>
    <ReactFlowProvider>
      <DnDProvider>
        <div className="dndflow" style={{width: '99%', height: '700px', borderTopLeftRadius: '20px', borderBottomLeftRadius: '20px'}}>
          <div
            className="reactflow-wrapper"
            style={{borderWidth: '4px', borderColor: '#466060', width: '400px', height: '650px', borderTopLeftRadius: '20px', borderBottomLeftRadius: '20px'}}
            >
            <Flow />
          </div>
          <Sidebar />
        </div>
      </DnDProvider>
    </ReactFlowProvider>
    <Modal
        open={showHelp}
        onClose={() => {setShowHelp(false)}}
        aria-labelledby="modal-title"
        aria-describedby="modal-description"
        className="flex items-center justify-center"
      >
        <StyledBox>
          <Typography id="modal-title" variant="h6" component="h2" className="text-center mb-4">
            {t('help')}
          </Typography>
          <Typography id="modal-description" sx={{ mb: 2 }}>
              {t('helpmsg')}
          </Typography>
          <Box className="flex justify-end">
            <Button onClick={() => {setShowHelp(false)}} variant="contained" color="secondary">
              Close
            </Button>
          </Box>
        </StyledBox>
      </Modal>
    </>
  );
}
