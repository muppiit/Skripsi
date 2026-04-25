/* eslint-disable no-unused-vars */
import React, { useState, useEffect, useRef } from "react";
import { Card, Button, Table, message, Divider } from "antd";
import {
  getAssessmentCriterias,
  deleteAssessmentCriteria,
  editAssessmentCriteria,
  addAssessmentCriteria,
} from "@/api/assessmentCriteria";
import TypingCard from "@/components/TypingCard";
import EditAssessmentCriteriaForm from "./forms/edit-assessmentCriteria-form";
import AddAssessmentCriteriaForm from "./forms/add-assessmentCriteria-form";

const { Column } = Table;

const AssessmentCriteria = () => {
  const [assessmentCriterias, setAssessmentCriterias] = useState([]);
  const [
    editAssessmentCriteriaModalVisible,
    setEditAssessmentCriteriaModalVisible,
  ] = useState(false);
  const [
    editAssessmentCriteriaModalLoading,
    setEditAssessmentCriteriaModalLoading,
  ] = useState(false);
  const [currentRowData, setCurrentRowData] = useState({});
  const [
    addAssessmentCriteriaModalVisible,
    setAddAssessmentCriteriaModalVisible,
  ] = useState(false);
  const [
    addAssessmentCriteriaModalLoading,
    setAddAssessmentCriteriaModalLoading,
  ] = useState(false);

  const editAssessmentCriteriaFormRef = useRef();
  const addAssessmentCriteriaFormRef = useRef();

  const fetchAssessmentCriterias = async () => {
    try {
      const result = await getAssessmentCriterias();
      const { content, statusCode } = result.data;

      if (statusCode === 200) {
        setAssessmentCriterias(content);
      } else {
        message.error("Gagal mengambil data");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  const handleEditAssessmentCriteria = (row) => {
    setCurrentRowData({ ...row });
    setEditAssessmentCriteriaModalVisible(true);
  };

  const handleDeleteAssessmentCriteria = async (row) => {
    const { id } = row;
    if (id === "admin") {
      message.error("Tidak dapat menghapus Admin!");
      return;
    }

    try {
      await deleteAssessmentCriteria({ id });
      message.success("Berhasil dihapus");
      fetchAssessmentCriterias();
    } catch (error) {
      message.error("Gagal menghapus: " + error.message);
    }
  };

  const handleEditAssessmentCriteriaOk = async (values) => {
    try {
      setEditAssessmentCriteriaModalLoading(true);
      const { id, ...dataToSend } = values; // Jangan kirim 'id' ke payload backend
      await editAssessmentCriteria(dataToSend, currentRowData.id);
      
      setEditAssessmentCriteriaModalVisible(false);
      message.success("Berhasil diubah!");
      fetchAssessmentCriterias();
    } catch (error) {
      console.error(error);
      message.error("Gagal mengubah: " + (error.response?.data?.message || error.message));
    } finally {
      setEditAssessmentCriteriaModalLoading(false);
    }
  };

  const handleCancel = () => {
    setEditAssessmentCriteriaModalVisible(false);
    setAddAssessmentCriteriaModalVisible(false);
  };

  const handleAddAssessmentCriteria = () => {
    setAddAssessmentCriteriaModalVisible(true);
  };

  const handleAddAssessmentCriteriaOk = async (values) => {
    try {
      setAddAssessmentCriteriaModalLoading(true);
      await addAssessmentCriteria(values);
      
      setAddAssessmentCriteriaModalVisible(false);
      message.success("Berhasil ditambahkan!");
      fetchAssessmentCriterias();
    } catch (error) {
      console.error(error);
      message.error("Gagal menambahkan: " + (error.response?.data?.message || error.message));
    } finally {
      setAddAssessmentCriteriaModalLoading(false);
    }
  };

  useEffect(() => {
    fetchAssessmentCriterias();
  }, []);

  const title = (
    <span>
      <Button type="primary" onClick={handleAddAssessmentCriteria}>
        Tambahkan penilaian
      </Button>
    </span>
  );

  const cardContent = `Di sini, Anda dapat mengelola penilaian di sistem, seperti menambahkan penilaian baru, atau mengubah penilaian yang sudah ada di sistem.`;

  return (
    <div className="app-container">
      <TypingCard title="Manajemen Penilaian" source={cardContent} />
      <br />
      <Card title={title}>
        <Table
          variant
          rowKey="id"
          dataSource={assessmentCriterias}
          pagination={false}
        >
          <Column title="ID Penilaian" dataIndex="id" key="id" align="center" />
          <Column title="Nama" dataIndex="name" key="name" align="center" />
          <Column
            title="Deskripsi Penilaian"
            dataIndex="description"
            key="description"
            align="center"
          />
          <Column
            title="Operasi"
            key="action"
            width={195}
            align="center"
            render={(text, row) => (
              <span>
                <Button
                  type="primary"
                  shape="circle"
                  icon="edit"
                  title="mengedit"
                  onClick={() => handleEditAssessmentCriteria(row)}
                />
                <Divider type="vertical" />
                <Button
                  type="primary"
                  shape="circle"
                  icon="delete"
                  title="menghapus"
                  onClick={() => handleDeleteAssessmentCriteria(row)}
                />
              </span>
            )}
          />
        </Table>
      </Card>

      <EditAssessmentCriteriaForm
        currentRowData={currentRowData}
        wrappedComponentRef={editAssessmentCriteriaFormRef}
        visible={editAssessmentCriteriaModalVisible}
        confirmLoading={editAssessmentCriteriaModalLoading}
        onCancel={handleCancel}
        onOk={handleEditAssessmentCriteriaOk}
      />

      <AddAssessmentCriteriaForm
        wrappedComponentRef={addAssessmentCriteriaFormRef}
        visible={addAssessmentCriteriaModalVisible}
        confirmLoading={addAssessmentCriteriaModalLoading}
        onCancel={handleCancel}
        onOk={handleAddAssessmentCriteriaOk}
      />
    </div>
  );
};

export default AssessmentCriteria;
