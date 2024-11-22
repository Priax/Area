"use client";

import React, { useState, useEffect, MouseEvent } from 'react';
import { getCookie } from 'cookies-next';
import { useRouter } from 'next/navigation';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Typography, Box, IconButton, Menu, MenuItem, Modal
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import SearchOffIcon from '@mui/icons-material/SearchOff';
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';
import InfoIcon from '@mui/icons-material/Info';
import { useTranslations } from 'next-intl';

interface ActionData {
  id: number;
  actionName: string;
  service: string;
  action: string;
  date: string;
}

interface ReactionData {
  id: number;
  service: string;
  values: {
    Reaction: string;
  }
}

export default function TaskTable() {
  const router = useRouter();
  const [data, setData] = useState<ActionData[]>([]);
  const [reactionData, setReactionData] = useState<ReactionData[]>([]);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [selectedItem, setSelectedItem] = useState<ActionData | null>(null);
  const [showDetails, setShowDetails] = useState<boolean>(false);

  const t = useTranslations('TasksPage');

  useEffect(() => {
    const userToken = getCookie("userToken") as string;
    const userId = getCookie("clientId") as string;
    if (!userToken && !userId) {
      const currentLocale = (getCookie("NEXT_LOCALE") as string) || "en";
      router.push(`/${currentLocale}/login`);
    }
  }, [router]);

  const getAllActions = async () => {
    try {
      const response = await fetch('http://localhost:8080/actions/user', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + (getCookie('userToken') as string),
          'UserId': String(getCookie('clientId')),
          "X-Request-ID": String(getCookie('clientId')),
        }
      });

      if (!response.ok) {
        console.error("Error when fetching", response);
        return;
      }

      const dataTaken = await response.json();

      const transformedData = dataTaken.map((item: any): ActionData => ({
        id: item.id,
        service: item.service,
        actionName: JSON.parse(item.values).actionName || "Action",
        action: JSON.parse(item.values).Action,
        date: item.date
      }));

      if (transformedData.length === 0) {
        return;
      }

      setData(transformedData);
    } catch (error) {
      console.error("Error", error);
    }
  };

  const DeleteAction = async (id: number) => {
    try {
      const response = await fetch('http://localhost:8080/actionreaction/delete', {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + (getCookie('userToken') as string),
          'UserID': String(getCookie('clientId')),
          "ActionId": String(id),
        },
      });

      if (!response.ok) {
        console.error("Error when fetching", response);
        return;
      }

      location.reload();
    } catch (error) {
      console.error("Error", error);
    }
  };

  const getReactions = async (item: ActionData) => {
    try {
      const response = await fetch('http://localhost:8080/reactions/action', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + (getCookie('userToken') as string),
          'UserID': String(getCookie('clientId')),
          "RequestID": String(item.id),
        }
      });

      if (!response.ok) {
        console.error("Error while fetching", await response.json());
        return;
      }

      const data = await response.json();
      const transformedReactions: ReactionData[] = data.map((item: any) => ({
        id: item.id,
        service: item.service,
        values: {
          Reaction: JSON.parse(item.values).Reaction,
        },
      }));

      setSelectedItem(item);
      console.log(selectedItem)
      setReactionData(transformedReactions);
    } catch (error) {
      console.error("Error", error);
    }
  };

  const handleMenuOpen = (event: MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedItem(null);
  };

  useEffect(() => {
    getAllActions();
  }, []);

  return (
    data.length > 0 ? (
      <>
        <Modal
          open={showDetails}
          onClose={() => setShowDetails(false)}
          className="flex items-center justify-center"
          >
          <Box
            sx={{
              background: "linear-gradient(to right, #D05353, #6A0DAD)",
              borderRadius: "12px",
              padding: "4px",
            }}
            >
            <Box sx={{ backgroundColor: 'white', padding: '20px' }}>
              <Typography variant="h6" className='text-3xl'>Details</Typography>
              <Box sx={{ mt: 2 }}>
                <Typography variant="h6" sx={{ color: '#333', fontWeight: 'bold', mb: 1 }}>
                  {t('solo')} {selectedItem?.actionName} - Action [{selectedItem?.action}] ({selectedItem?.service})
                </Typography>
                {reactionData.length > 0 ? (
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mt: 2 }}>
                    {reactionData.map((reaction: ReactionData, index: number) => (
                      <Box key={index} sx={{
                        padding: '8px 12px',
                        borderRadius: '6px',
                        backgroundColor: '#e0f7fa',
                        display: 'flex',
                        alignItems: 'center',
                      }}>
                        <Typography variant="body2" sx={{ fontWeight: 'bold', color: '#00796b' }}>
                          {reaction.values.Reaction}
                        </Typography>
                        <Typography variant="body2" sx={{ color: '#555', ml: 0.5 }}>
                          ({reaction.service})
                        </Typography>
                        {index < reactionData.length - 1 && (
                          <Typography variant="body2" sx={{ mx: 1, color: '#777' }}>
                            →
                          </Typography>
                        )}
                      </Box>
                    ))}
                  </Box>
                ) : (
                  <Typography variant="body2" sx={{ color: '#888', mt: 1 }}>
                    Ø
                  </Typography>
                )}
              </Box>
            </Box>
          </Box>
        </Modal>

        <Typography component="h2" variant="h6" sx={{ mb: 3, mt: 1 }} className='text-3xl'>
          {t('table')}
        </Typography>
        <TableContainer component={Paper} className="shadow-lg rounded-lg mt-6">
          <Table>
            <TableHead className="bg-gradient-to-r from-pink-700 to-purple-600">
              <TableRow>
                <TableCell className="text-white font-bold">ID</TableCell>
                <TableCell className="text-white font-bold">Service</TableCell>
                <TableCell className="text-white font-bold">{t('name')}</TableCell>
                <TableCell className="text-white font-bold">Date</TableCell>
                <TableCell className="text-white font-bold"></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {data.map((item) => (
                <TableRow key={item.id} className="hover:bg-blue-50">
                  <TableCell className="border-b border-gray-200">{item.id}</TableCell>
                  <TableCell className="border-b border-gray-200">{item.service}</TableCell>
                  <TableCell className="border-b border-gray-200">{item.actionName}</TableCell>
                  <TableCell className="border-b border-gray-200">{item.date}</TableCell>
                  <TableCell className="border-b border-gray-200">
                    <IconButton
                      onClick={(event) => handleMenuOpen(event)}
                      aria-label="More options"
                      className="hover:bg-gray-100 transition-colors"
                      >
                      <MoreHorizIcon />
                    </IconButton>
                    <Menu
                      anchorEl={anchorEl}
                      open={Boolean(anchorEl)}
                      onClose={handleMenuClose}
                      >
                      <MenuItem onClick={() => {
                        getReactions(item);
                        setShowDetails(true);
                        handleMenuClose();
                      }}>
                        <InfoIcon sx={{ color: '#5a5a5a', mr: 1 }} />
                        {t('details')}
                      </MenuItem>
                      <MenuItem onClick={() => { DeleteAction(item.id); handleMenuClose(); }}>
                        <DeleteIcon sx={{ color: '#D05353', mr: 1 }} />
                        {t('delete')}
                      </MenuItem>
                    </Menu>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </>
    ) : (
      <Box className="flex flex-col items-center justify-center h-64 rounded-lg shadow-md">
        <SearchOffIcon sx={{ fontSize: 50, color: '#000' }} />
        <Typography variant="h6" className="mt-4 mb-3 text-gray-700">
          {t('notasks')}
        </Typography>
        <Typography variant="body1" className="text-gray-500">
          {t('createmsg')} <a className='text-blue-500 font-bold' href={`/${getCookie("NEXT_LOCALE") || "en"}/tasks`}>{t('createlink')}</a>.
        </Typography>
      </Box>
    )
  );
}
