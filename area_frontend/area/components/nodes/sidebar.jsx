import React from 'react';
import { useDnD } from './dnd';
import { Typography } from '@mui/material';
import ReplyIcon from '@mui/icons-material/Reply';
import { useTranslations } from 'next-intl';
export default () => {
  const [_, setType] = useDnD();

  const onDragStart = (event, nodeType) => {
    setType(nodeType);
    event.dataTransfer.effectAllowed = 'move';
  };
  const t = useTranslations('TasksPage');
return (
  
  
  <aside
    style={{
      height: '650px',
      width: '50%',
      backgroundColor: '#F9F3DB',
      borderRight: '4px solid #466060',
      borderTop: '4px solid #466060',
      borderBottom: '4px solid #466060',
      borderTopRightRadius: '20px',
      borderBottomRightRadius: '20px',
      padding: '16px',
      display: 'flex',
      flexDirection: 'column',
      gap: '4px',
    }}
  >
    <div className="description" style={{ backgroundColor: '#F9F3DB' }}>
      <Typography component="h2" variant="h6" sx={{ mb: 2 }}>
        <ReplyIcon/> {t('draganddrop')}
      </Typography>
    </div>

    <div
      className="dndnode input bg-white"
      style={{
        height: 'auto',
        padding: '16px',
        display: 'flex',
        flexDirection: 'column',
        gap: '8px',
        borderRadius: '8px',
        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
        border: '2px solid red',
        cursor: 'pointer',
      }}
      onDragStart={(event) => onDragStart(event, 'selectReaction')}
      draggable
    >
      <div>
        <p className="font-bold text-lg">{t('titlenew')}</p>
      </div>
      <div>
        <label htmlFor="input-demo" style={{fontSize: '15px', lineHeight: '10px'}}>{t('service')}</label>
        <input
          name="input-demo"
          disabled
          placeholder={t('noservice')}
          style={{
            width: '100%',
            padding: '8px',
            border: '1px solid #ccc',
            borderRadius: '4px',
            marginTop: '8px',
            color: '#999',
          }}
        />
      </div>
      <div>
        <label htmlFor="input-demo" style={{fontSize: '15px', lineHeight: '10px'}}>{t('reaction')}</label>
        <input
          name="input-demo"
          disabled
          placeholder={t('noreaction')}
          style={{
            width: '100%',
            padding: '8px',
            border: '1px solid #ccc',
            marginTop: '8px',
            color: '#999',
          }}
        />
      </div>
    </div>
  </aside>
  );
};